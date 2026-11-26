export class MetricStore {
  constructor(api, session) {
    this.api = api;
    this.session = session;
  }

  async render(container) {
    if (!container) return;
    container.innerHTML = '<div class="card empty-state">Carregando métricas...</div>';

    try {
      const pets = await this.api.request(`/api/pets/user/${this.session.user.id}`);
      if (!pets.length) {
        container.innerHTML = '<div class="card empty-state">Cadastre um animal para acompanhar suas métricas.</div>';
        return;
      }

      container.innerHTML = `<div class="metric-toolbar"><label>Animal<select id="metricPetSelect">${pets.map(pet => `<option value="${pet.id}">${this.escapeHtml(pet.name)}</option>`).join('')}</select></label><label>Período<select id="metricPeriod"><option value="30">Últimos 30 dias</option><option value="90">Últimos 90 dias</option><option value="365">Último ano</option></select></label></div><div id="metricContent"></div>`;
      const petSelect = container.querySelector('#metricPetSelect');
      const periodSelect = container.querySelector('#metricPeriod');
      const load = () => this.loadMetric(Number(petSelect.value), Number(periodSelect.value), container.querySelector('#metricContent'));
      petSelect.onchange = load;
      periodSelect.onchange = load;
      load();
    } catch (error) {
      container.innerHTML = `<div class="card empty-state">${error.message}</div>`;
    }
  }

  async loadMetric(petId, period, container) {
    container.innerHTML = '<div class="card empty-state">Atualizando histórico...</div>';
    const finalDate = new Date();
    const initialDate = new Date();
    initialDate.setDate(initialDate.getDate() - period);

    try {
      const dateInitial = this.toIsoDate(initialDate);
      const dateFinal = this.toIsoDate(finalDate);
      const metrics = await this.api.request(`/api/pets/${petId}/metrics?dateInitial=${dateInitial}&dateFinal=${dateFinal}`);

      if (!metrics.length) {
        container.innerHTML = '<div class="card empty-state">Nenhuma medição registrada neste período.</div>';
        return;
      }

      const latest = metrics[metrics.length - 1];
      const first = metrics[0];
      container.innerHTML = `<div class="metric-summary"><div class="metric-card card"><span>Peso atual</span><strong>${latest.weight} kg</strong><small>${this.weightVariation(first.weight, latest.weight)}</small></div><div class="metric-card card"><span>Altura atual</span><strong>${latest.height} cm</strong><small>${this.heightVariation(first.height, latest.height)}</small></div></div><div class="metric-chart card"><div class="chart-head"><h3>Evolução do peso</h3><span>kg</span></div>${this.lineChart(metrics)}</div><div class="metric-list card"><h3>Histórico</h3>${metrics.slice().reverse().map(metric => `<div class="metric-row"><span>${this.formatDate(metric.measuredAt)}</span><strong>${metric.weight} kg</strong><span>${metric.height} cm</span></div>`).join('')}</div>`;
    } catch (error) {
      container.innerHTML = `<div class="card empty-state">${error.message}</div>`;
    }
  }

  lineChart(metrics) {
    if (metrics.length === 1) {
      return `<div class="single-point"><span style="bottom:${this.chartPosition(metrics[0].weight, metrics)}%"></span><small>${this.formatDate(metrics[0].measuredAt)}</small></div>`;
    }

    const width = 640;
    const height = 180;
    const padding = 20;
    const weights = metrics.map(item => item.weight);
    const minimum = Math.min(...weights);
    const maximum = Math.max(...weights);
    const range = maximum - minimum || 1;
    const points = metrics.map((item, index) => {
      const x = padding + index * (width - padding * 2) / (metrics.length - 1);
      const y = height - padding - (item.weight - minimum) / range * (height - padding * 2);
      return `${x.toFixed(1)},${y.toFixed(1)}`;
    }).join(' ');

    return `<svg class="chart-svg" viewBox="0 0 ${width} ${height}" role="img" aria-label="Gráfico de evolução do peso"><line x1="20" y1="160" x2="620" y2="160"/><polyline points="${points}"/><circle cx="${points.split(' ').at(-1).split(',')[0]}" cy="${points.split(' ').at(-1).split(',')[1]}" r="5"/></svg>`;
  }

  chartPosition(weight, metrics) {
    const weights = metrics.map(item => item.weight);
    const minimum = Math.min(...weights);
    const range = Math.max(...weights) - minimum || 1;
    return 30 + (weight - minimum) / range * 40;
  }

  weightVariation(first, latest) {
    const variation = latest - first;
    return `${variation >= 0 ? '+' : ''}${variation.toFixed(1)} kg no período`;
  }

  heightVariation(first, latest) {
    const variation = latest - first;
    return `${variation >= 0 ? '+' : ''}${variation.toFixed(1)} cm no período`;
  }

  formatDate(value) {
    return new Date(value).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  toIsoDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  escapeHtml(value) {
    return String(value || '').replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }
}
