const icons={plus:'<svg class="icon small" viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>',calendar:'<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M7 3v4M17 3v4M3 10h18"/></svg>'};

export class PageRenderer {
  constructor(petStore, appointmentStore, metricStore, memoryStore, adminStore, profileStore) {
    this.petStore = petStore;
    this.appointmentStore = appointmentStore;
    this.metricStore = metricStore;
    this.memoryStore = memoryStore;
    this.adminStore = adminStore;
    this.profileStore = profileStore;
  }

  render(route) {
    const main = document.querySelector('#main');
    const pages = { inicio: () => this.inicio(), animais: () => this.animals(), saude: () => this.health(), memorias: () => this.memories(), admin: () => this.admin(), perfil: () => this.profile() };
    main.innerHTML = (pages[route] || pages.inicio)();
    this.bindPetActions();
    if (route === 'inicio' || route === 'animais') this.petStore.list(document.querySelector('#petGrid'));
    if (route === 'saude') {
      this.appointmentStore.list(document.querySelector('#appointmentGrid'));
      this.metricStore.render(document.querySelector('#metricGrid'));
      document.querySelector('#newAppointmentButton')?.addEventListener('click', () =>
        this.appointmentStore.openCreateModal(() => this.appointmentStore.list(document.querySelector('#appointmentGrid')))
      );
    }
    if (route === 'memorias') {
      this.memoryStore.render(document.querySelector('#memoryGrid'));
      document.querySelector('#newMemoryButton')?.addEventListener('click', () =>
        this.memoryStore.openCreateModal(() => this.memoryStore.render(document.querySelector('#memoryGrid')))
      );
    }
    if (route === 'admin') {
      this.adminStore.render(document.querySelector('#adminGrid'));
    }
    if (route === 'perfil') {
      this.profileStore.render(document.querySelector('#profileGrid'));
    }
  }

  inicio() {
    return `<div class="breadcrumb-row"><div class="breadcrumb"></div><div class="demo-label"><i></i>Painel de cuidados</div></div><div class="page-head"><div><h1>Que bom ter você por aqui!</h1><p>Um lugar para cuidar de quem deixa a sua vida mais feliz.</p></div></div><section class="hero card"><div class="hero-copy"><h2>Todo o carinho, em um só lugar.</h2><p>Acompanhe a saúde, organize os cuidados e guarde as melhores memórias dos seus animais.</p><div class="hero-actions"><button class="gold-btn" id="newPetButton">${icons.plus} Registrar um animal</button><button class="outline-btn" onclick="location.hash='#/memorias'">${icons.calendar} Guardar uma memória</button></div></div><div class="hero-image"></div></section><div class="section-head"><h2>Meus animais</h2><button class="outline-btn" id="addPetButton">${icons.plus} Adicionar animal</button></div><div class="pet-grid" id="petGrid"><div class="card empty-state">Carregando seus animais...</div></div>`;
  }

  animals() {
    return `<div class="breadcrumb-row"><div class="breadcrumb">Início / Animais</div></div><div class="page-head"><div><h1>Animais</h1><p>Veja e gerencie os perfis dos seus companheiros.</p></div><button class="gold-btn" id="newPetButton">${icons.plus} Adicionar animal</button></div><div class="section-head"><h2>Meus animais</h2></div><div class="pet-grid" id="petGrid"><div class="card empty-state">Carregando seus animais...</div></div>`;
  }

  health() {
    return `<div class="breadcrumb-row"><div class="breadcrumb">Início / Saúde</div></div><div class="page-head"><div><h1>Saúde</h1><p>Organize consultas, exames e acompanhe as medidas dos seus animais.</p></div><button class="gold-btn" id="newAppointmentButton">${icons.plus} Registrar agendamento</button></div><div class="section-head"><h2>Próximos cuidados</h2></div><div class="appointment-grid" id="appointmentGrid"><div class="card empty-state">Carregando agendamentos...</div></div><div class="section-head metric-section-head"><h2>Métricas de saúde</h2></div><div id="metricGrid"><div class="card empty-state">Carregando métricas...</div></div>`;
  }

  memories() {
    return `<div class="breadcrumb-row"><div class="breadcrumb">Início / Memórias</div></div><div class="page-head"><div><h1>Memórias</h1><p>Guarde momentos especiais ao lado dos seus animais.</p></div><button class="gold-btn" id="newMemoryButton">${icons.plus} Nova memória</button></div><div id="memoryGrid"><div class="card empty-state">Carregando memórias...</div></div>`;
  }

  admin() {
    return `<div class="breadcrumb-row"><div class="breadcrumb">Início / Administração</div></div><div class="page-head"><div><h1>Usuários</h1><p>Gerencie perfis, acessos e status das contas.</p></div></div><div id="adminGrid"><div class="card empty-state">Carregando usuários...</div></div>`;
  }

  profile() {
    return `<div class="breadcrumb-row"><div class="breadcrumb">Início / Meu perfil</div></div><div class="page-head"><div><h1>Meu perfil</h1><p>Atualize seus dados e credenciais.</p></div></div><div id="profileGrid"></div>`;
  }

  bindPetActions() {
    document.querySelectorAll('#newPetButton, #addPetButton').forEach(button => {
      button.onclick = () => this.petStore.openCreateModal(() => this.petStore.list(document.querySelector('#petGrid')));
    });
  }
}
