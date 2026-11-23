let user, csrf, equipment = [], reservations = [], reportRows = [];
let activeView = 'catalog';
const $ = selector => document.querySelector(selector);
const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]));
const dateText = value => value ? new Date(value).toLocaleString('es-PE', {dateStyle:'short',timeStyle:'short'}) : '—';
function notify(message, error = false) {
    $('#notice').textContent = message; $('#notice').className = error ? 'error' : ''; $('#notice').hidden = false;
    clearTimeout(notify.timer); notify.timer = setTimeout(() => $('#notice').hidden = true, 6500);
}
async function token() { csrf = await (await fetch('/api/csrf')).json(); }
async function api(path, body, form = false) {
    const options = {headers:{}};
    if (body !== undefined) {
        options.method = 'POST'; options.headers[csrf.header] = csrf.token;
        options.headers['Content-Type'] = form ? 'application/x-www-form-urlencoded' : 'application/json';
        options.body = form ? new URLSearchParams(body) : JSON.stringify(body);
    }
    const response = await fetch('/api' + path, options);
    if (!response.ok) {
        if (response.status === 401 && path !== '/login') showLogin();
        const data = await response.json().catch(() => ({}));
        throw new Error(data.message || ({401:'Usuario o contraseña incorrectos.',403:'Acceso denegado. Reingresa si tu sesión expiró.'}[response.status]) || 'No se pudo completar la operación. Revisa los datos.');
    }
    const raw = await response.text(); return raw ? JSON.parse(raw) : null;
}
function showLogin() { user = null; $('#workspace').hidden = true; $('#login-view').hidden = false; $('#modal').close(); }
async function signedIn() {
    user = await api('/me'); await token();
    $('#login-view').hidden = true; $('#workspace').hidden = false;
    $('#user-name').textContent = user.NAME; $('#user-role').textContent = user.ROLE;
    document.querySelectorAll('[data-admin]').forEach(el => el.hidden = user.ROLE !== 'ENCARGADO');
    await showView('catalog');
}
async function showView(view) {
    activeView = view;
    document.querySelectorAll('.view').forEach(el => el.hidden = el.id !== view);
    document.querySelectorAll('[data-view]').forEach(el => el.classList.toggle('active', el.dataset.view === view));
    if (view === 'catalog') await loadEquipment(false);
    if (view === 'equipment') await inventory();
    if (view === 'mine' || view === 'manage') await loadReservations();
    if (view === 'report') await loadReport();
}
function statusPill(status) {
    const color = ['PENDIENTE','PRESTADO','MANTENIMIENTO'].includes(status) ? 'amber' : ['RECHAZADA','CANCELADA','VENCIDO'].includes(status) ? 'red' : '';
    return `<span class="pill ${color}">${escapeHtml(status)}</span>`;
}
async function loadEquipment(filtered) {
    let query = '';
    if (filtered) query = '?' + new URLSearchParams(new FormData($('#availability')));
    equipment = await api('/equipment' + query);
    $('#equipment-count').textContent = equipment.length + ' equipos';
    $('#equipment-cards').innerHTML = equipment.map(e => {
        const available = filtered ? e.AVAILABLE : e.STATE === 'OPERATIVO';
        return `<article class="card"><div class="equipment-top"><span class="equipment-code">${escapeHtml(e.CODE)}</span>${statusPill(e.STATE === 'MANTENIMIENTO' ? e.STATE : filtered ? (available ? 'DISPONIBLE' : 'RESERVADO') : 'OPERATIVO')}</div><div class="card-body"><h2>${escapeHtml(e.NAME)}</h2><p>${escapeHtml(e.CATEGORY)}</p><small class="muted">${filtered ? 'Disponibilidad para el horario consultado' : 'Consulta un horario antes de reservar'}</small><button data-reserve="${e.ID}" ${available ? '' : 'disabled'}>Solicitar reserva</button></div></article>`;
    }).join('') || '<div class="empty">No hay equipos registrados.</div>';
}
function openModal(title, html, submit) {
    $('#modal-title').textContent = title; $('#modal-form').innerHTML = html + '<button type="submit">Guardar</button>';
    $('#modal-form').onsubmit = async event => {
        event.preventDefault(); const button = event.submitter; button.disabled = true;
        try { await submit(new FormData(event.target)); $('#modal').close(); notify('Operación guardada.'); await showView(activeView); }
        catch (error) { notify(error.message, true); }
        finally { button.disabled = false; }
    };
    $('#modal').showModal();
}
function reserve(id) {
    const item = equipment.find(e => e.ID === id);
    const interval = new FormData($('#availability'));
    openModal('Reservar ' + item.NAME, `<p>${escapeHtml(item.CODE)} · ${escapeHtml(item.CATEGORY)}</p><label>Inicio<input name="start" type="datetime-local" value="${escapeHtml(interval.get('start'))}" required></label><label>Fin<input name="end" type="datetime-local" value="${escapeHtml(interval.get('end'))}" required></label><label>Motivo<textarea name="reason" maxlength="500" required rows="3"></textarea></label>`, async form => {
        await api('/reservations', {equipmentId:id, ...Object.fromEntries(form)});
    });
}
function rowsHtml(rows, actions = false) {
    if (!rows.length) return '<div class="empty">No hay registros para mostrar.</div>';
    return `<div class="table-wrap"><table><thead><tr><th>Solicitud / equipo</th><th>Estudiante</th><th>Horario</th><th>Estado</th><th>Detalle${actions ? ' / acciones' : ''}</th></tr></thead><tbody>${rows.map(r => {
        const overdue = r.STATUS === 'PRESTADO' && new Date(r.ENDS_AT) < new Date();
        let buttons = '';
        if (actions && activeView === 'mine' && ['PENDIENTE','APROBADA'].includes(r.STATUS) && new Date(r.STARTS_AT)>new Date()) buttons = `<button class="secondary" data-action="cancel" data-id="${r.ID}">Cancelar</button>`;
        if (actions && activeView === 'manage') {
            if (r.STATUS === 'PENDIENTE') buttons = `<button data-action="approve" data-id="${r.ID}">Aprobar</button><button class="danger" data-action="reject" data-id="${r.ID}">Rechazar</button>`;
            if (r.STATUS === 'APROBADA') buttons = `<button data-action="deliver" data-id="${r.ID}">Registrar entrega</button>`;
            if (r.STATUS === 'PRESTADO') buttons = `<button data-action="return" data-id="${r.ID}">Registrar devolución</button>`;
        }
        return `<tr><td><strong>#${r.ID} · ${escapeHtml(r.EQUIPMENT_NAME)}</strong><small>${escapeHtml(r.CODE)}</small></td><td>${escapeHtml(r.STUDENT_NAME)}</td><td>${dateText(r.STARTS_AT)}<small>Hasta ${dateText(r.ENDS_AT)}</small></td><td>${statusPill(overdue?'VENCIDO':r.STATUS)}</td><td>${escapeHtml(r.REASON)}${r.DECISION_NOTE ? '<small>Decisión: '+escapeHtml(r.DECISION_NOTE)+'</small>':''}${r.DELIVERED_AT ? '<small>Entrega: '+dateText(r.DELIVERED_AT)+' · '+escapeHtml(r.INITIAL_CONDITION)+'</small>':''}${r.RETURNED_AT ? '<small>Devolución: '+dateText(r.RETURNED_AT)+' · '+escapeHtml(r.FINAL_CONDITION)+'</small>':''}<div>${buttons}</div></td></tr>`;
    }).join('')}</tbody></table></div>`;
}
async function loadReservations() {
    const admin = activeView === 'manage'; reservations = await api(admin ? '/admin/reservations' : '/reservations');
    const status = $('#manage-status').value;
    $(admin ? '#all-reservations' : '#my-reservations').innerHTML = rowsHtml(reservations.filter(r => !admin || !status || r.STATUS === status), true);
}
function action(id, action) {
    const titles = {cancel:'Cancelar reserva',approve:'Aprobar solicitud',reject:'Rechazar solicitud',deliver:'Registrar entrega',return:'Registrar devolución'};
    let fields = `<p>Solicitud #${id}. Confirma la operación.</p>`;
    if (['reject','deliver','return'].includes(action)) fields += `<label>${action === 'reject' ? 'Motivo del rechazo' : 'Condición del equipo'}<textarea name="note" maxlength="500" required rows="3"></textarea></label>`;
    if (action === 'return') fields += '<label class="check"><input type="checkbox" name="damaged">El equipo requiere mantenimiento</label>';
    openModal(titles[action], fields, async form => {
        const path = action === 'cancel' ? `/reservations/${id}/cancel` : `/admin/reservations/${id}/${action}`;
        await api(path,{note:form.get('note') || '',damaged:form.has('damaged')});
    });
}
async function inventory() {
    equipment = await api('/equipment');
    $('#inventory').innerHTML = `<div class="table-wrap"><table><thead><tr><th>Código</th><th>Equipo</th><th>Categoría</th><th>Estado</th><th>Acción</th></tr></thead><tbody>${equipment.map(e=>`<tr><td>${escapeHtml(e.CODE)}</td><td>${escapeHtml(e.NAME)}</td><td>${escapeHtml(e.CATEGORY)}</td><td>${statusPill(e.STATE)}</td><td><button class="secondary" data-edit="${e.ID}">Editar</button></td></tr>`).join('')}</tbody></table></div>`;
}
function editEquipment(id) {
    const e = equipment.find(e=>e.ID===id) || {};
    openModal(id ? 'Editar equipo' : 'Agregar equipo', `<label>Código<input name="code" maxlength="40" value="${escapeHtml(e.CODE)}" required></label><label>Nombre<input name="name" maxlength="120" value="${escapeHtml(e.NAME)}" required></label><label>Categoría<input name="category" maxlength="80" value="${escapeHtml(e.CATEGORY)}" required></label><label>Estado<select name="state"><option ${e.STATE==='OPERATIVO'?'selected':''}>OPERATIVO</option><option ${e.STATE==='MANTENIMIENTO'?'selected':''}>MANTENIMIENTO</option></select></label>`, async form => api('/admin/equipment',{id:id || null,...Object.fromEntries(form)}));
}
async function loadReport() {
    const query = new URLSearchParams();
    for (const [key,value] of new FormData($('#report-filter'))) if (value) query.set(key,value);
    const report = await api('/admin/report?' + query); reportRows = report.rows;
    $('#report-summary').innerHTML = [[report.total,'Registros'],[report.open,'Préstamos abiertos'],[report.overdue,'Vencidos']].map(([value,label])=>`<div class="stat"><strong>${value}</strong><span>${label}</span></div>`).join('');
    $('#report-rows').innerHTML = rowsHtml(report.rows);
}
function exportCsv() {
    const cells = value => '"' + String(value ?? '').replace(/^[=+@-]/,"'$&").replaceAll('"','""') + '"';
    const lines = [['ID','Equipo','Estudiante','Inicio','Fin','Estado'],...reportRows.map(r=>[r.ID,r.EQUIPMENT_NAME,r.STUDENT_NAME,r.STARTS_AT,r.ENDS_AT,r.STATUS])];
    const url = URL.createObjectURL(new Blob(['\ufeff'+lines.map(row=>row.map(cells).join(',')).join('\r\n')],{type:'text/csv;charset=utf-8'}));
    const a = document.createElement('a'); a.href=url; a.download='labprestamo-reporte.csv'; a.click(); URL.revokeObjectURL(url);
}
$('#login-form').onsubmit = async event => {
    event.preventDefault(); event.submitter.disabled = true;
    try { await token(); await api('/login',Object.fromEntries(new FormData(event.target)),true); event.target.reset(); await signedIn(); }
    catch(error) { notify(error.message,true); } finally { event.submitter.disabled=false; }
};
$('#logout').onclick = async () => { try { await api('/logout',{}); showLogin(); await token(); } catch(e) { notify(e.message,true); } };
$('#close-modal').onclick = () => $('#modal').close();
$('#availability').onsubmit = event => { event.preventDefault(); loadEquipment(true).catch(e=>notify(e.message,true)); };
$('#clear-filter').onclick = () => { $('#availability').reset(); loadEquipment(false).catch(e=>notify(e.message,true)); };
$('#report-filter').onsubmit = event => { event.preventDefault(); loadReport().catch(e=>notify(e.message,true)); };
$('#manage-status').onchange = () => loadReservations().catch(e=>notify(e.message,true));
$('#new-equipment').onclick = () => editEquipment(null);
$('#export').onclick = exportCsv;
document.addEventListener('click', event => {
    const b = event.target.closest('button'); if (!b) return;
    if (b.dataset.view) showView(b.dataset.view).catch(e=>notify(e.message,true));
    if (b.dataset.reserve) reserve(Number(b.dataset.reserve));
    if (b.dataset.edit) editEquipment(Number(b.dataset.edit));
    if (b.dataset.action) action(Number(b.dataset.id),b.dataset.action);
});
$('#today').textContent = new Date().toLocaleDateString('es-PE',{dateStyle:'long'});
(async () => { try { await token(); await signedIn(); } catch { showLogin(); } })();
