export class AppointmentStore {
  constructor(api, session) {
    this.api = api;
    this.session = session;
  }

  async list(container) {
    if (!container) return;
    container.innerHTML = '<div class="card empty-state">Carregando agendamentos...</div>';

    try {
      const pets = await this.api.request(`/api/pets/user/${this.session.user.id}`);
      if (!pets.length) {
        container.innerHTML = '<div class="card empty-state">Cadastre um animal antes de criar agendamentos.</div>';
        return;
      }

      const appointmentsByPet = await Promise.all(pets.map(async pet => {
        const appointments = await this.api.request(`/api/appointments/pet/${pet.id}`);
        return { pet, appointments };
      }));

      const appointments = appointmentsByPet.flatMap(({ pet, appointments }) =>
        appointments.map(appointment => ({ ...appointment, petName: pet.name }))
      ).sort((a, b) => new Date(a.date) - new Date(b.date));

      if (!appointments.length) {
        container.innerHTML = '<div class="card empty-state">Nenhum agendamento encontrado.</div>';
        return;
      }

      container.innerHTML = appointments.map(item => this.cardTemplate(item)).join('');
      container.querySelectorAll('.edit-appointment').forEach(button =>
        button.onclick = () => this.openModal(pets, Number(button.dataset.id), () => this.list(container)));
      container.querySelectorAll('.delete-appointment').forEach(button =>
        button.onclick = () => this.deleteAppointment(Number(button.dataset.id), () => this.list(container)));
    } catch (error) {
      container.innerHTML = `<div class="card empty-state">${error.message}</div>`;
    }
  }

  cardTemplate(item) {
    const date = new Date(item.date);
    const dateText = date.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' });
    const timeText = date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    return `<article class="card appointment-card"><div class="appointment-date"><strong>${dateText}</strong><span>${timeText}</span></div><div class="appointment-content"><small>${item.petName}</small><h3>${this.escapeHtml(item.title)}</h3><p>${this.escapeHtml(item.description)}</p></div><div class="appointment-actions"><button type="button" class="text-link edit-appointment" data-id="${item.id}">Editar</button><button type="button" class="text-link danger-link delete-appointment" data-id="${item.id}">Excluir</button></div></article>`;
  }

  async openModal(pets, appointmentId = null, onSaved) {
    let appointment = null;
    try {
      if (appointmentId) appointment = await this.api.request(`/api/appointments/${appointmentId}`);

      const backdrop = document.createElement('div');
      backdrop.className = 'modal-backdrop';
      backdrop.innerHTML = `<section class="modal"><h2>${appointment ? 'Editar agendamento' : 'Novo agendamento'}</h2><form id="appointmentForm"><div class="field"><label>Animal</label><select name="petId" required ${appointment ? 'disabled' : ''}>${pets.map(pet => `<option value="${pet.id}" ${appointment?.petId === pet.id ? 'selected' : ''}>${this.escapeHtml(pet.name)}</option>`).join('')}</select></div><div class="field"><label>Título</label><div class="input-wrap"><input name="title" required maxlength="100" value="${this.escapeAttribute(appointment?.title || '')}"></div></div><div class="field"><label>Descrição</label><div class="input-wrap"><input name="description" required maxlength="300" value="${this.escapeAttribute(appointment?.description || '')}"></div></div><div class="field"><label>Data e horário</label><div class="input-wrap"><input name="date" type="datetime-local" required value="${appointment ? this.toDateTimeLocal(appointment.date) : ''}"></div></div><div class="modal-actions"><button type="button" class="cancel">Cancelar</button><button type="submit" class="submit">${appointment ? 'Salvar alterações' : 'Salvar agendamento'}</button></div></form></section>`;
      document.body.append(backdrop);

      backdrop.querySelector('.cancel').onclick = () => backdrop.remove();
      backdrop.onclick = event => { if (event.target === backdrop) backdrop.remove(); };
      backdrop.querySelector('#appointmentForm').onsubmit = async event => {
        event.preventDefault();
        const form = new FormData(event.target);
        const date = form.get('date') + ':00';
        try {
          if (appointment) {
            await this.api.request('/api/appointments', { method: 'PUT', body: JSON.stringify({ id: appointmentId, title: form.get('title'), description: form.get('description'), date }) });
          } else {
            await this.api.request('/api/appointments', { method: 'POST', body: JSON.stringify({ title: form.get('title'), description: form.get('description'), date, petId: Number(form.get('petId')) }) });
          }
          backdrop.remove();
          onSaved?.();
        } catch (error) { alert(error.message); }
      };
    } catch (error) {
      alert(error.message);
    }
  }

  async openCreateModal(onSaved) {
    const pets = await this.api.request(`/api/pets/user/${this.session.user.id}`);
    if (!pets.length) {
      alert('Cadastre um animal antes de criar agendamentos.');
      return;
    }
    await this.openModal(pets, null, onSaved);
  }

  async deleteAppointment(appointmentId, onDeleted) {
    if (!confirm('Deseja excluir este agendamento?')) return;
    try {
      await this.api.request(`/api/appointments/${appointmentId}`, { method: 'DELETE' });
      onDeleted?.();
    } catch (error) { alert(error.message); }
  }

  toDateTimeLocal(value) {
    const date = new Date(value);
    const offset = date.getTimezoneOffset();
    return new Date(date.getTime() - offset * 60000).toISOString().slice(0, 16);
  }

  escapeHtml(value) {
    return String(value || '').replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }

  escapeAttribute(value) {
    return this.escapeHtml(value).replaceAll('"', '&quot;');
  }
}
