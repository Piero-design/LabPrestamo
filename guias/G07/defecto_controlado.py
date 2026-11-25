#!/usr/bin/env python3
"""Inyecta el defecto de intervalo igual, exige un fallo JUnit y restaura siempre."""
import os, platform, subprocess, shutil
from pathlib import Path
root=Path(__file__).resolve().parents[2];source=root/'src/main/java/pe/labprestamo/LabService.java'
out=root/'guias/G07/evidencias';out.mkdir(parents=True,exist_ok=True)
env=os.environ.copy()
if platform.system()=='Darwin':env['JAVA_HOME']=subprocess.check_output(['/usr/libexec/java_home'],text=True).strip()
mvn=shutil.which('mvn') or str(Path.home()/'.maven/maven-3.9.11/bin/mvn')
original=source.read_bytes();old=b'!end.isAfter(start)';new=b'end.isBefore(start)'
assert original.count(old)==1,'Regla inesperada; revisar antes de inyectar'
try:
 source.write_bytes(original.replace(old,new))
 with (out/'defecto-controlado.txt').open('w') as log:
  result=subprocess.run([mvn,'-Dtest=ReservationTest#PU12_equalIntervalRejected','test'],cwd=root,env=env,stdout=log,stderr=subprocess.STDOUT)
 report=root/'target/surefire-reports/pe.labprestamo.ReservationTest.txt'
 text=report.read_text();(out/'defecto-junit.txt').write_text(text)
 if result.returncode==0 or 'Failures: 1' not in text or 'nothing was thrown' not in text:
  raise SystemExit('No se detectó el fallo esperado; revisar evidencia')
 print('Defecto detectado por JUnit: intervalo de longitud cero aceptado.')
finally:
 source.write_bytes(original)
print('Fuente restaurada. Ejecutando regresión completa...')
with (out/'restauracion.txt').open('w') as log:
 result=subprocess.run([mvn,'clean','test'],cwd=root,env=env,stdout=log,stderr=subprocess.STDOUT)
raise SystemExit(result.returncode)
