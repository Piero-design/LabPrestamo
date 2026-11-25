"""G06: prueba HTTP de controles en LabPrestamo LOCAL de demostración.
Python 3.9+; sin dependencias externas. Genera evidencia_controles.json.
Crea una reserva de prueba persistente. No usar con datos de producción.
"""
import concurrent.futures
import datetime as dt
import http.cookiejar
import json
import pathlib
import subprocess
import threading
import urllib.error
import urllib.parse
import urllib.request
import uuid

BASE = 'http://localhost:8080/api'
OUT = pathlib.Path(__file__).resolve().with_name('evidencia_controles.json')

def commit():
    try:
        return subprocess.check_output(['git','rev-parse','HEAD'], cwd=OUT.parent, text=True, stderr=subprocess.DEVNULL).strip()
    except (OSError, subprocess.CalledProcessError):
        return 'no identificado'

class Session:
    def __init__(self):
        self.opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        self.csrf = None
    def req(self, path, data=None, form=False):
        headers = {'Accept':'application/json'}
        body = None
        if data is not None:
            if self.csrf:
                headers[self.csrf['header']] = self.csrf['token']
            headers['Content-Type'] = 'application/x-www-form-urlencoded' if form else 'application/json'
            body = (urllib.parse.urlencode(data) if form else json.dumps(data)).encode()
        request = urllib.request.Request(BASE + path, data=body, headers=headers)
        try:
            with self.opener.open(request, timeout=20) as response:
                status, raw = response.status, response.read().decode()
        except urllib.error.HTTPError as exc:
            status, raw = exc.code, exc.read().decode(errors='replace')
        try:
            value = json.loads(raw) if raw else None
        except ValueError:
            value = raw[:300]
        return status, value
    def token(self):
        status, value = self.req('/csrf')
        if status != 200 or not isinstance(value, dict) or not {'header','token'} <= value.keys():
            raise RuntimeError(f'No se obtuvo token CSRF: {status}')
        self.csrf = value
    def login(self, user, password):
        self.token()
        status, _ = self.req('/login', {'username':user,'password':password}, form=True)
        if status != 204:
            raise RuntimeError(f'Login {user}: HTTP {status}')
        self.token()
        status, me = self.req('/me')
        if status != 200:
            raise RuntimeError(f'Sesión no comprobada: {status}')
        return me

def checked_rows(session, path):
    status, rows = session.req(path)
    if status != 200 or not isinstance(rows, list):
        raise RuntimeError(f'{path}: HTTP {status}; no se obtuvo lista')
    return rows

def main():
    evidence = {'fecha':dt.datetime.now().astimezone().isoformat(), 'commit':commit(), 'base':BASE, 'casos':{}, 'nota':'Solo entorno local; se conserva reserva de prueba.'}
    try:
        s1,s2,admin = Session(),Session(),Session()
        evidence['roles'] = [s1.login('estudiante','Estudiante123!').get('ROLE'), s2.login('estudiante2','Estudiante123!').get('ROLE'), admin.login('encargado','Encargado123!').get('ROLE')]
        now = dt.datetime.now(dt.timezone(dt.timedelta(hours=-5))).replace(tzinfo=None, second=0, microsecond=0)
        for offset in range(7,22):
            start = now + dt.timedelta(days=offset)
            end = start + dt.timedelta(minutes=30)
            a,b = start.isoformat(timespec='minutes'),end.isoformat(timespec='minutes')
            query = urllib.parse.urlencode({'start':a,'end':b})
            free = [r for r in checked_rows(s1,'/equipment?'+query) if r.get('AVAILABLE') is True]
            if free:
                equipment = free[0]['ID']; break
        else:
            raise RuntimeError('No hay equipos disponibles en los intervalos consultados')
        marker = 'G06-'+uuid.uuid4().hex[:12]
        payload = {'equipmentId':equipment,'start':a,'end':b,'reason':marker}
        evidence['datos'] = payload
        barrier = threading.Barrier(2)
        def send(session):
            barrier.wait(timeout=10)
            return session.req('/reservations',payload)[0]
        with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
            futures = [pool.submit(send,s) for s in (s1,s2)]
            statuses = [f.result() for f in futures]
        matching = [r for r in checked_rows(admin,'/admin/reservations') if r.get('REASON') == marker]
        ct2 = sum(200 <= s < 300 for s in statuses)==1 and statuses.count(409)==1 and len(matching)==1
        evidence['casos']['CT-02'] = {'HTTP':statuses,'registros':len(matching),'IDs':[r.get('ID') for r in matching],'estado':'APROBADO' if ct2 else 'FALLIDO'}
        if len(matching)==1:
            rid = matching[0]['ID']
            before = matching[0]['STATUS']
            # The student who created the reservation attempts a forbidden approval.
            owner = s1 if 200 <= statuses[0] < 300 else s2
            status, _ = owner.req(f'/admin/reservations/{rid}/approve',{'note':'','damaged':False})
            admin_status, admin_rows = admin.req('/admin/reservations')
            after = next((r.get('STATUS') for r in admin_rows if r.get('ID')==rid),None) if isinstance(admin_rows,list) else None
            ct1 = status==403 and before==after=='PENDIENTE' and admin_status==200
            evidence['casos']['CT-01'] = {'reserva':rid,'HTTP_estudiante':status,'HTTP_listado_encargado':admin_status,'antes':before,'despues':after,'estado':'APROBADO' if ct1 else 'FALLIDO'}
        else:
            evidence['casos']['CT-01'] = {'estado':'NO EJECUTADO','motivo':'no se obtuvo una única reserva pendiente de prueba'}
    except Exception as exc:
        evidence['error'] = f'{type(exc).__name__}: {exc}'
    for case in ('CT-01','CT-02'):
        evidence['casos'].setdefault(case,{'estado':'NO EJECUTADO'})
    OUT.write_text(json.dumps(evidence,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print(json.dumps(evidence,ensure_ascii=False,indent=2))
    print('Evidencia:',OUT)
    return 0 if all(c['estado']=='APROBADO' for c in evidence['casos'].values()) and not evidence.get('error') else 1

if __name__=='__main__':
    raise SystemExit(main())
