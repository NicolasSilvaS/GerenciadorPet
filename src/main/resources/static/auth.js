const icons = {
  paw: '<svg class="icon" viewBox="0 0 24 24"><circle cx="7" cy="8" r="2"/><circle cx="17" cy="8" r="2"/><circle cx="12" cy="5" r="2"/><path d="M12 10c-3 0-6 3-6 6 0 2 2 3 4 2l2-1 2 1c2 1 4 0 4-2 0-3-3-6-6-6Z"/></svg>',
  user: '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 21c1-5 15-5 16 0"/></svg>',
  mail: '<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 6 9-6"/></svg>',
  lock: '<svg class="icon" viewBox="0 0 24 24"><rect x="5" y="10" width="14" height="11" rx="2"/><path d="M8 10V7a4 4 0 018 0v3"/></svg>',
  eye: '<svg class="icon small" viewBox="0 0 24 24"><path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/></svg>'
};

export class AuthView {
  constructor(api, session) {
    this.api = api;
    this.session = session;
  }

  render(mode) {
    const register = mode === 'register';
    const app = document.querySelector('#app');
    app.innerHTML = `<main class="auth-page ${register ? 'register' : 'login'}">
      <section class="auth-card">
        <div class="brand-mark">${icons.paw}</div>
        <h1>${register ? 'Join the family' : 'Welcome back'}</h1>
        <p class="subtitle">${register ? 'Create an account to start tracking pet care' : 'Please enter your details to sign in'}</p>
        <form id="authForm">
          ${register ? `<div class="field"><label>Full name</label><div class="input-wrap">${icons.user}<input name="username" required maxlength="100" placeholder="John Doe"></div></div>` : ''}
          <div class="field"><label>${register ? 'Email address' : 'Username'}</label><div class="input-wrap">${register ? icons.mail : icons.user}<input name="${register ? 'email' : 'username'}" ${register ? 'type="email"' : ''} required maxlength="100" placeholder="${register ? 'you@example.com' : 'Seu usuário'}"></div></div>
          <div class="field"><label>Password</label><div class="input-wrap">${icons.lock}<input name="password" type="password" required minlength="6" maxlength="100" placeholder="••••••••"><button type="button" class="link eye">${icons.eye}</button></div></div>
          ${register ? `<div class="field"><label>Confirm Password</label><div class="input-wrap">${icons.lock}<input name="confirm" type="password" required minlength="6" placeholder="••••••••"></div></div><div class="terms"><label><input name="terms" type="checkbox" required> I agree to the Terms and Privacy</label></div>` : '<div class="row"><label class="check-row"><input type="checkbox" checked> Remember me</label><button type="button" class="link forgot">Forgot password?</button></div>'}
          <div class="error" id="authError"></div>
          <div class="auth-actions"><button class="btn-primary">${register ? 'Create Account' : 'Sign in'}</button></div>
        </form>
        <p class="auth-switch">${register ? 'Already have an account?' : 'Don’t have an account?'} <button class="link switch">${register ? 'Log in' : 'Sign up'}</button></p>
      </section>
    </main>`;

    document.querySelector('.switch').onclick = () => location.hash = register ? '#/login' : '#/register';
    document.querySelectorAll('.eye').forEach(button => button.onclick = () => {
      const input = button.parentElement.querySelector('input');
      input.type = input.type === 'password' ? 'text' : 'password';
    });

    const forgot = document.querySelector('.forgot');
    if (forgot) forgot.onclick = () => this.recoverPassword();
    document.querySelector('#authForm').onsubmit = event => this.submit(event, register);
  }

  async recoverPassword() {
    const email = prompt('Informe seu email cadastrado');
    if (!email) return;
    try {
      await this.api.request('/api/users/forgot-password', { method: 'POST', body: JSON.stringify({ email }) });
      alert('Se houver uma conta ativa, enviaremos as instruções por email.');
    } catch (error) { alert(error.message); }
  }

  async submit(event, register) {
    event.preventEventDefault?.();
    event.preventDefault();
    const form = new FormData(event.target);
    const error = document.querySelector('#authError');
    error.textContent = '';

    try {
      if (register) {
        if (form.get('password') !== form.get('confirm')) throw new Error('As senhas não conferem');
        await this.api.request('/api/users/register', {
          method: 'POST',
          body: JSON.stringify({ email: form.get('email'), username: form.get('username'), password: form.get('password') })
        });
        location.hash = '#/login';
      } else {
        const data = await this.api.request('/api/users/login', {
          method: 'POST',
          body: JSON.stringify({ username: form.get('username'), password: form.get('password') })
        });
        this.session.login(data);
        location.hash = '#/inicio';
      }
    } catch (requestError) { error.textContent = requestError.message; }
  }
}

export class Session {
  constructor() {
    this.token = localStorage.getItem('lacostoken');
    this.user = JSON.parse(localStorage.getItem('lacosuser') || 'null');
  }
  isAuthenticated() {
    if (!this.token) return false;
    try {
      const payload = JSON.parse(atob(this.token.split('.')[1].replaceAll('-', '+').replaceAll('_', '/')));
      if (payload.exp && payload.exp * 1000 <= Date.now()) {
        this.logout();
        return false;
      }
      return true;
    } catch {
      return false;
    }
  }
  login(data) {
    this.token = data.accessToken;
    this.user = { id: data.id, username: data.username, email: data.email, admin: data.admin };
    localStorage.setItem('lacostoken', this.token);
    localStorage.setItem('lacosuser', JSON.stringify(this.user));
  }
  logout() {
    this.token = null;
    this.user = null;
    localStorage.removeItem('lacostoken');
    localStorage.removeItem('lacosuser');
  }
}
