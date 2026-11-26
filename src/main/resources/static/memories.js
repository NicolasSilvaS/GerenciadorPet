export class MemoryStore {
  constructor(api, session) {
    this.api = api;
    this.session = session;
  }

  async render(container) {
    if (!container) return;
    container.innerHTML = '<div class="card empty-state">Carregando memórias...</div>';

    try {
      const pets = await this.api.request(`/api/pets/user/${this.session.user.id}`);
      if (!pets.length) {
        container.innerHTML = '<div class="card empty-state">Cadastre um animal para guardar memórias.</div>';
        return;
      }

      container.innerHTML = `<div class="memory-toolbar"><label>Animal<select id="memoryPetSelect"><option value="">Todos os animais</option>${pets.map(pet => `<option value="${pet.id}">${this.escapeHtml(pet.name)}</option>`).join('')}</select></label></div><div class="memory-grid" id="memoryGrid"></div>`;
      const petSelect = container.querySelector('#memoryPetSelect');
      petSelect.onchange = () => this.load(petSelect.value, pets, container.querySelector('#memoryGrid'));
      this.load('', pets, container.querySelector('#memoryGrid'));
    } catch (error) {
      container.innerHTML = `<div class="card empty-state">${error.message}</div>`;
    }
  }

  async load(petId, pets, container) {
    container.innerHTML = '<div class="card empty-state">Atualizando memórias...</div>';
    try {
      const selected = petId
        ? [{ pet: pets.find(pet => pet.id === Number(petId)), records: await this.api.request(`/api/historics/pet/${petId}`) }]
        : await Promise.all(pets.map(async pet => ({
            pet,
            records: await this.api.request(`/api/historics/pet/${pet.id}`)
          })));

      const records = selected.flatMap(({ pet, records }) =>
        records.map(record => ({ ...record, petName: pet.name }))
      ).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

      if (!records.length) {
        container.innerHTML = '<div class="card empty-state">Nenhuma memória registrada.</div>';
        return;
      }

      container.innerHTML = records.map(record => this.cardTemplate(record)).join('');
      container.querySelectorAll('.delete-memory').forEach(button => {
        button.onclick = () => this.deleteRecord(Number(button.dataset.id), () => this.load(petId, pets, container));
      });
    } catch (error) {
      container.innerHTML = `<div class="card empty-state">${error.message}</div>`;
    }
  }

  cardTemplate(record) {
    const date = new Date(record.createdAt).toLocaleDateString('pt-BR', { day: '2-digit', month: 'long', year: 'numeric' });
    const isMeasurement = record.type === 'MEASUREMENT';
    return `<article class="card memory-card"><div class="memory-icon ${isMeasurement ? 'measurement' : ''}">${isMeasurement ? '📏' : '♡'}</div><div class="memory-content"><div class="memory-meta"><span>${this.escapeHtml(record.petName)}</span><time>${date}</time></div><p>${this.escapeHtml(record.description)}</p></div><button type="button" class="text-link danger-link delete-memory" data-id="${record.id}">Excluir</button></article>`;
  }

  openCreateModal(onSaved) {
    this.api.request(`/api/pets/user/${this.session.user.id}`).then(pets => {
      if (!pets.length) {
        alert('Cadastre um animal antes de guardar memórias.');
        return;
      }

      const backdrop = document.createElement('div');
      backdrop.className = 'modal-backdrop';
      backdrop.innerHTML = `<section class="modal"><h2>Nova memória</h2><form id="memoryForm"><div class="field"><label>Animal</label><select name="petId" required>${pets.map(pet => `<option value="${pet.id}">${this.escapeHtml(pet.name)}</option>`).join('')}</select></div><div class="field"><label>Tipo</label><select name="type" required><option value="IDENITITY">Momento especial</option><option value="MEASUREMENT">Marco ou medição</option></select></div><div class="field"><label>Descrição</label><textarea name="description" required maxlength="300" rows="4" placeholder="Conte um momento especial..."></textarea></div><div class="modal-actions"><button type="button" class="cancel">Cancelar</button><button type="submit" class="submit">Salvar memória</button></div></form></section>`;
      document.body.append(backdrop);

      backdrop.querySelector('.cancel').onclick = () => backdrop.remove();
      backdrop.onclick = event => { if (event.target === backdrop) backdrop.remove(); };
      backdrop.querySelector('#memoryForm').onsubmit = async event => {
        event.preventEventDefault?.();
        event.preventDefault();
        const form = new FormData(event.target);
        try {
          await this.api.request('/api/historics', { method: 'POST', body: JSON.stringify({
            description: form.get('description'),
            type: form.get('type'),
            petId: Number(form.get('petId'))
          }) });
          backdrop.remove();
          onSaved?.();
        } catch (error) { alert(error.message); }
      };
    }).catch(error => alert(error.message));
  }

  async deleteRecord(recordId, onDeleted) {
    if (!confirm('Deseja excluir esta memória?')) return;
    try {
      await this.api.request(`/api/historics/${recordId}`, { method: 'DELETE' });
      onDeleted?.();
    } catch (error) { alert(error.message); }
  }

  escapeHtml(value) {
    return String(value || '').replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }
}
