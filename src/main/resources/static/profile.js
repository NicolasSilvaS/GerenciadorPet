export class ProfileStore {
  constructor(api, session) {
    this.api = api;
    this.session = session;
  }

  render(container) {
    const user = this.session.user;
    container.innerHTML = `<div class="profile-layout"><section class="card profile-card"><div class="profile-avatar">${this.initials(user?.username)}</div><h2>${this.escapeHtml(user?.username || 'Usuário')}</h2><p>${this.escapeHtml(user?.email || '')}</p><span class="role-badge ${user?.admin === 'ADMIN' ? 'admin' : ''}">${user?.admin === 'ADMIN' ? 'Administrador' : 'Usuário padrão'}</span></section><section class="card profile-form-card"><h2>Dados da conta</h2><form id="profileForm"><div class="field"><label>Nome de usuário</label><div class="input-wrap"><input name="username" maxlength="100" required value="${this.escapeAttribute(user?.username || '')}"></div></div><div class="field"><label>Email</label><div class="input-wrap"><input name="email" type="email" maxlength="100" required value="${this.escapeAttribute(user?.email || '')}"></div></div><div class="field"><label>Nova senha</label><div class="input-wrap"><input name="password" type="password" minlength="8" maxlength="100" required placeholder="Digite uma nova senha"></div></div><div id="profileStatus" class="form-status" role="status" aria-live="polite"></div><div class="profile-actions"><button type="button" class="outline-btn" id="profileLogout">Sair da conta</button><button type="submit" class="gold-btn">Salvar alterações</button></div></form></section></div>`;

    container.querySelector('#profileForm').onsubmit = event => this.submit(event, container);
    container.querySelector('#profileLogout').onclick = () => {
      if (confirm('Deseja sair da sua conta?')) {
        this.session.logout();
        location.hash = '#/login';
      }
    };
  }

  async submit(event, container) {
    event.preventDefault();
    const form = new FormData(event.target);
    const status = container.querySelector('#profileStatus');
    status.textContent = 'Salvando alterações...';

    try {
      const updated = await this.api.request('/api/users', {
        method: 'PUT',
        body: JSON.stringify({
          id: this.session.user.id,
          username: form.get('username'),
          email: form.get('email'),
          password: form.get('password'),
          admin: this.session.user.admin,
          active: true
        })
      });
      this.session.user = { ...this.session.user, id: updated.id, username: updated.username, email: updated.email, admin: updated.admin };
      localStorage.setItem('lacosuser', JSON.stringify(this.session.user));
      status.textContent = 'Perfil atualizado com sucesso.';
      this.render(container);
      this.showToast('Perfil atualizado com sucesso.');
    } catch (error) {
      status.textContent = error.message;
      this.showToast(error.message, true);
    }
  }

  initials(value) {
    return String(value || 'U').split(/\s+/).map(part => part[0]).join('').slice(0, 2).toUpperCase();
  }

  showToast(message, error = false) {
    document.querySelector('.toast')?.remove();
    const toast = document.createElement('div');
    toast.className = `toast ${error ? 'error' : ''}`;
    toast.textContent = message;
    document.body.append(toast);
    setTimeout(() => toast.remove(), 3500);
  }

  escapeHtml(value) {
    return String(value || '').replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }

  escapeAttribute(value) {
    return this.escapeHtml(value).replaceAll('"', '&quot;');
  }
}
