export class AdminStore {
  constructor(api, session) {
    this.api = api;
    this.session = session;
  }

  async render(container) {
    if (!container) return;
    container.innerHTML = '<div class="card empty-state">Carregando usuários...</div>';

    try {
      const users = await this.api.request('/api/admin/users');
      if (!users.length) {
        container.innerHTML = '<div class="card empty-state">Nenhum usuário encontrado.</div>';
        return;
      }

      container.innerHTML = `<div class="admin-toolbar"><div><strong>${users.length}</strong><span> usuário(s)</span></div><button type="button" class="gold-btn" id="newAdminUserButton">+ Novo usuário</button></div><div class="admin-table card"><div class="admin-row admin-header"><span>Usuário</span><span>Email</span><span>Perfil</span><span>Status</span><span>Ações</span></div>${users.map(user => this.rowTemplate(user)).join('')}</div>`;

      container.querySelector('#newAdminUserButton').onclick = () => this.openCreateModal(() => this.render(container));
      container.querySelectorAll('.edit-admin-user').forEach(button => {
        button.onclick = () => this.openEditModal(Number(button.dataset.id), () => this.render(container));
      });
      container.querySelectorAll('.deactivate-admin-user').forEach(button => {
        button.onclick = () => this.deactivate(Number(button.dataset.id), () => this.render(container));
      });
    } catch (error) {
      container.innerHTML = `<div class="card empty-state">${error.message}</div>`;
    }
  }

  rowTemplate(user) {
    const admin = user.admin === 'ADMIN';
    return `<div class="admin-row"><span><strong>${this.escapeHtml(user.username)}</strong></span><span>${this.escapeHtml(user.email)}</span><span><b class="role-badge ${admin ? 'admin' : ''}">${admin ? 'Administrador' : 'Padrão'}</b></span><span><b class="status-badge ${user.active ? 'active' : 'inactive'}">${user.active ? 'Ativo' : 'Inativo'}</b></span><span class="admin-actions"><button type="button" class="text-link edit-admin-user" data-id="${user.id}">Editar</button>${user.active ? `<button type="button" class="text-link danger-link deactivate-admin-user" data-id="${user.id}">Desativar</button>` : ''}</span></div>`;
  }

  openCreateModal(onSaved) {
    this.openModal(null, onSaved);
  }

  async openEditModal(userId, onSaved) {
    try {
      const user = await this.api.request(`/api/admin/users/${userId}`);
      this.openModal(user, onSaved);
    } catch (error) { alert(error.message); }
  }

  openModal(user, onSaved) {
    const editing = Boolean(user);
    const backdrop = document.createElement('div');
    backdrop.className = 'modal-backdrop';
    backdrop.innerHTML = `<section class="modal"><h2>${editing ? 'Editar usuário' : 'Novo usuário'}</h2><form id="adminUserForm"><div class="field"><label>Nome de usuário</label><div class="input-wrap"><input name="username" required maxlength="100" value="${this.escapeAttribute(user?.username || '')}"></div></div><div class="field"><label>Email</label><div class="input-wrap"><input name="email" type="email" required maxlength="100" value="${this.escapeAttribute(user?.email || '')}"></div></div><div class="field"><label>Senha${editing ? ' (nova senha)' : ''}</label><div class="input-wrap"><input name="password" type="password" required maxlength="100"></div></div><div class="row"><div class="field" style="width:48%"><label>Perfil</label><select name="admin" required><option value="STANDARD" ${user?.admin !== 'ADMIN' ? 'selected' : ''}>Padrão</option><option value="ADMIN" ${user?.admin === 'ADMIN' ? 'selected' : ''}>Administrador</option></select></div><div class="field" style="width:48%"><label>Status</label><select name="active" required><option value="true" ${user?.active !== false ? 'selected' : ''}>Ativo</option><option value="false" ${user?.active === false ? 'selected' : ''}>Inativo</option></select></div></div><div class="modal-actions"><button type="button" class="cancel">Cancelar</button><button type="submit" class="submit">${editing ? 'Salvar alterações' : 'Criar usuário'}</button></div></form></section>`;
    document.body.append(backdrop);

    backdrop.querySelector('.cancel').onclick = () => backdrop.remove();
    backdrop.onclick = event => { if (event.target === backdrop) backdrop.remove(); };
    backdrop.querySelector('#adminUserForm').onsubmit = async event => {
      event.preventDefault();
      const form = new FormData(event.target);
      const payload = {
        id: user?.id,
        username: form.get('username'),
        email: form.get('email'),
        password: form.get('password'),
        admin: form.get('admin'),
        active: form.get('active') === 'true'
      };

      try {
        if (editing) {
          await this.api.request('/api/admin/users', { method: 'PUT', body: JSON.stringify({ id: user.id, username: form.get('username'), admin: payload.admin === 'ADMIN', active: payload.active }) });
        } else {
          await this.api.request('/api/admin/users', { method: 'POST', body: JSON.stringify(payload) });
        }
        backdrop.remove();
        onSaved?.();
      } catch (error) { alert(error.message); }
    };
  }

  async deactivate(userId, onChanged) {
    if (!confirm('Deseja desativar este usuário?')) return;
    try {
      await this.api.request(`/api/admin/users/${userId}/deactivate`, { method: 'PATCH' });
      onChanged?.();
    } catch (error) { alert(error.message); }
  }

  escapeHtml(value) {
    return String(value || '').replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }

  escapeAttribute(value) {
    return this.escapeHtml(value).replaceAll('"', '&quot;');
  }
}
