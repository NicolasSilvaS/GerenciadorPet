export class PetStore {
  constructor(api, session) {
    this.api = api;
    this.session = session;
  }

  async list(container) {
    if (!container) return;
    try {
      const pets = await this.api.request(`/api/pets/user/${this.session.user?.id}`);
      if (!pets.length) {
        container.innerHTML = '<div class="card empty-state">Nenhum animal cadastrado. Adicione o primeiro companheiro.</div>';
        return;
      }
      container.innerHTML = pets.map((pet, index) => this.cardTemplate(pet, index)).join('');
      this.bindCardActions(container, () => this.list(container));
    } catch (error) {
      container.innerHTML = `<div class="card empty-state">${error.message}</div>`;
    }
  }

  cardTemplate(pet, index) {
    const images = ['https://images.unsplash.com/photo-1558788353-f76d92427f16?auto=format&fit=crop&w=300&q=80','https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80'];
    return `<article class="pet-card card" data-pet-id="${pet.id}"><div class="pet-summary"><img class="pet-photo" src="${images[index % images.length]}" alt="${pet.name}"><div class="pet-info"><div class="pet-title"><h3>${pet.name}</h3></div><div class="breed">${pet.breed}</div><p class="desc">Companheiro de todos os momentos.</p><div class="stats"><div class="stat"><small>Peso</small><strong>${pet.weight} kg</strong></div><div class="stat"><small>Altura</small><strong>${pet.height} cm</strong></div></div></div></div><div class="pet-footer"><span>Perfil cadastrado</span><div class="pet-actions"><button type="button" class="text-link edit-pet" data-pet-id="${pet.id}">Editar</button><button type="button" class="text-link delete-pet danger-link" data-pet-id="${pet.id}">Excluir</button></div></div></article>`;
  }

  openCreateModal(onSaved) {
    const backdrop = document.createElement('div');
    backdrop.className = 'modal-backdrop';
    backdrop.innerHTML = `<section class="modal"><h2>Adicionar animal</h2><form id="petForm"><div class="field"><label>Nome</label><div class="input-wrap"><input name="name" required maxlength="100"></div></div><div class="field"><label>Raça</label><div class="input-wrap"><input name="breed" required maxlength="100"></div></div><div class="row"><div class="field" style="width:48%"><label>Peso (kg)</label><div class="input-wrap"><input name="weight" type="number" min="0.1" step="0.1" required></div></div><div class="field" style="width:48%"><label>Altura (cm)</label><div class="input-wrap"><input name="height" type="number" min="0.1" step="0.1" required></div></div></div><div class="field"><label>Data de nascimento</label><div class="input-wrap"><input name="bornIn" type="date" required></div></div><div class="modal-actions"><button type="button" class="cancel">Cancelar</button><button type="submit" class="submit">Salvar animal</button></div></form></section>`;
    document.body.append(backdrop);

    backdrop.querySelector('.cancel').onclick = () => backdrop.remove();
    backdrop.onclick = event => { if (event.target === backdrop) backdrop.remove(); };
    backdrop.querySelector('#petForm').onsubmit = async event => {
      event.preventDefault();
      const form = new FormData(event.target);
      try {
        await this.api.request('/api/pets', { method: 'POST', body: JSON.stringify({
          name: form.get('name'), breed: form.get('breed'), weight: Number(form.get('weight')),
          height: Number(form.get('height')), userId: this.session.user.id,
          bornIn: form.get('bornIn') + 'T12:00:00'
        }) });
        backdrop.remove();
        onSaved?.();
      } catch (error) { alert(error.message); }
    };
  }

  async openEditModal(petId, onSaved) {
    try {
      const pet = await this.api.request(`/api/pets/${petId}`);
      const bornIn = pet.bornIn ? pet.bornIn.substring(0, 10) : '';
      const backdrop = document.createElement('div');
      backdrop.className = 'modal-backdrop';
      backdrop.innerHTML = `<section class="modal"><h2>Editar animal</h2><form id="petEditForm"><div class="field"><label>Nome</label><div class="input-wrap"><input name="name" required maxlength="100" value="${this.escapeAttribute(pet.name)}"></div></div><div class="field"><label>Raça</label><div class="input-wrap"><input name="breed" required maxlength="100" value="${this.escapeAttribute(pet.breed)}"></div></div><div class="row"><div class="field" style="width:48%"><label>Peso (kg)</label><div class="input-wrap"><input name="weight" type="number" min="0.1" step="0.1" required value="${pet.weight}"></div></div><div class="field" style="width:48%"><label>Altura (cm)</label><div class="input-wrap"><input name="height" type="number" min="0.1" step="0.1" required value="${pet.height}"></div></div></div><div class="field"><label>Data de nascimento</label><div class="input-wrap"><input name="bornIn" type="date" required value="${bornIn}"></div></div><div class="modal-actions"><button type="button" class="cancel">Cancelar</button><button type="submit" class="submit">Salvar alterações</button></div></form></section>`;
      document.body.append(backdrop);

      backdrop.querySelector('.cancel').onclick = () => backdrop.remove();
      backdrop.onclick = event => { if (event.target === backdrop) backdrop.remove(); };
      backdrop.querySelector('#petEditForm').onsubmit = async event => {
        event.preventDefault();
        const form = new FormData(event.target);
        try {
          await this.api.request('/api/pets', { method: 'PUT', body: JSON.stringify({
            id: petId,
            name: form.get('name'), breed: form.get('breed'), weight: Number(form.get('weight')),
            height: Number(form.get('height')), bornIn: form.get('bornIn') + 'T12:00:00'
          }) });
          backdrop.remove();
          onSaved?.();
        } catch (error) { alert(error.message); }
      };
    } catch (error) {
      alert(error.message);
    }
  }

  async deletePet(petId, onChanged) {
    if (!confirm('Deseja excluir este animal? Essa ação não pode ser desfeita.')) return;
    try {
      await this.api.request(`/api/pets/${petId}`, { method: 'DELETE' });
      onChanged?.();
    } catch (error) {
      alert(error.message);
    }
  }

  bindCardActions(container, onChanged) {
    container.querySelectorAll('.edit-pet').forEach(button => {
      button.onclick = () => this.openEditModal(Number(button.dataset.petId), onChanged);
    });
    container.querySelectorAll('.delete-pet').forEach(button => {
      button.onclick = () => this.deletePet(Number(button.dataset.petId), onChanged);
    });
  }

  escapeAttribute(value) {
    return String(value || '').replaceAll('&', '&amp;').replaceAll('"', '&quot;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }
}
