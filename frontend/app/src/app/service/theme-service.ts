import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  paleta = [
  '#9333ea', // roxo vibrante
  '#7e22ce', // roxo mais fechado
  '#6d28d9', // roxo profundo
  '#c026d3', // magenta
  '#db2777', // rosa escuro
  '#be185d', // rosa mais fechado
  '#9d174d', // vinho
  '#701a75', // roxo vinho
  '#581c87', // roxo bem escuro
  '#1e3a8a', // azul profundo
  '#312e81', // índigo escuro
  '#4c1d95', // roxo azulado
  '#831843', // pink escuro elegante
  '#a21caf', // roxo magenta forte
  '#86198f'  // roxo equilibrado
];

  getCorAleatoria(): string {
    const index = Math.floor(Math.random() * this.paleta.length);
    return this.paleta[index];
  }
}
