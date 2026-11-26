const icons={
  paw:'<svg class="icon" viewBox="0 0 24 24"><circle cx="7" cy="8" r="2"/><circle cx="17" cy="8" r="2"/><circle cx="12" cy="5" r="2"/><path d="M12 10c-3 0-6 3-6 6 0 2 2 3 4 2l2-1 2 1c2 1 4 0 4-2 0-3-3-6-6-6Z"/></svg>',
  home:'<svg class="icon" viewBox="0 0 24 24"><path d="M3 11l9-8 9 8"/><path d="M5 10v10h14V10M9 20v-6h6v6"/></svg>',
  health:'<svg class="icon" viewBox="0 0 24 24"><path d="M12 21l-7-7a5 5 0 017-7 5 5 0 017 7l-7 7Z"/><path d="M8 12h2l1-3 2 6 1-3h2"/></svg>',
  mem:'<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M7 3v4M17 3v4M3 10h18"/></svg>',
  pets:'<svg class="icon" viewBox="0 0 24 24"><circle cx="8" cy="8" r="2"/><circle cx="16" cy="8" r="2"/><circle cx="12" cy="5" r="2"/><path d="M12 10c-4 0-7 4-7 7 0 2 2 3 4 2l3-2 3 2c2 1 4 0 4-2 0-3-3-7-7-7Z"/></svg>',
  bell:'<svg class="icon" viewBox="0 0 24 24"><path d="M18 8a6 6 0 00-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4"/></svg>',
  user:'<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 21c1-5 15-5 16 0"/></svg>',
  admin:'<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="8" r="3"/><path d="M5 21c1-5 13-5 14 0M17 4l2 2 2-2M19 6v5"/></svg>'
};

export class AppShell {
  constructor(session) { this.session = session; }

  render() {
    document.querySelector('#app').innerHTML = `<div class="app-shell">
      <header class="topbar">
        <div class="brand"><div class="brand-badge">${icons.paw}</div><div class="brand-name">Laços<small>Cuidado que aproxima</small></div></div>
        <nav class="nav">
          <button data-page="inicio">${icons.home}<span>Início</span></button>
          <button data-page="saude">${icons.health}<span>Saúde</span></button>
          <button data-page="memorias">${icons.mem}<span>Memórias</span></button>
          <button data-page="animais">${icons.pets}<span>Animais</span></button>
          ${this.session.user?.admin === 'ADMIN' ? `<button data-page="admin">${icons.admin}<span>Administração</span></button>` : ''}
        </nav>
        <div class="top-actions"><button class="icon-button">${icons.bell}</button><button class="account" id="accountButton"><span class="avatar-dot">${icons.user}</span><span>${this.session.user?.username || 'Minha conta'}</span></button></div>
      </header>
      <main class="main" id="main"></main>
    </div>`;

    document.querySelectorAll('.nav button').forEach(button => {
      button.onclick = () => location.hash = '#/' + button.dataset.page;
    });
    document.querySelector('#accountButton').onclick = () => {
      location.hash = '#/perfil';
    };
  }

  setActive(route) {
    document.querySelectorAll('.nav button').forEach(button => button.classList.toggle('active', button.dataset.page === route));
  }
}
