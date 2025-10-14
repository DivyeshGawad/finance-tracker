import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { providePrimeNG } from 'primeng/config';
import Lara from '@primeng/themes/aura';  // ✅ re-enable theme

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimations(),   // ✅ only once
    providePrimeNG({
      theme: {
        preset: Lara,  // ✅ enables default Aura PrimeNG theme
      },
       ripple: true,
      
    }),
  ],
};
