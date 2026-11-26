import { ApiClient } from './api.js';
import { AuthView, Session } from './auth.js';
import { AppShell } from './shell.js';
import { PetStore } from './pets.js';
import { PageRenderer } from './pages.js';
import { AppointmentStore } from './appointments.js';
import { MetricStore } from './metrics.js';
import { MemoryStore } from './memories.js';
import { AdminStore } from './admin.js';
import { ProfileStore } from './profile.js';

class AppRouter {
  constructor() {
    this.session = new Session();
    this.api = new ApiClient(() => this.session.token, () => {
      this.session.logout();
      location.hash = '#/login';
    });
    this.auth = new AuthView(this.api, this.session);
    this.shell = new AppShell(this.session);
    this.pets = new PetStore(this.api, this.session);
    this.appointments = new AppointmentStore(this.api, this.session);
    this.metrics = new MetricStore(this.api, this.session);
    this.memories = new MemoryStore(this.api, this.session);
    this.admin = new AdminStore(this.api, this.session);
    this.profile = new ProfileStore(this.api, this.session);
    this.pages = new PageRenderer(this.pets, this.appointments, this.metrics, this.memories, this.admin, this.profile);
  }

  start() {
    window.addEventListener('hashchange', () => this.render());
    this.render();
  }

  render() {
    const hash = location.hash || (this.session.isAuthenticated() ? '#/inicio' : '#/login');
    const route = hash.replace('#/', '').replace('#', '');

    if (route === 'login' || route === 'register') {
      this.auth.render(route);
      return;
    }
    if (!this.session.isAuthenticated()) {
      location.hash = '#/login';
      return;
    }

    if (route === 'admin' && this.session.user?.admin !== 'ADMIN') {
      location.hash = '#/inicio';
      return;
    }

    if (!document.querySelector('.app-shell')) this.shell.render();
    this.shell.setActive(route || 'inicio');
    this.pages.render(route || 'inicio');
  }
}

new AppRouter().start();
