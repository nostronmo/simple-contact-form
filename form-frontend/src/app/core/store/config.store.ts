import { inject, PLATFORM_ID } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { isPlatformBrowser } from '@angular/common';

interface SystemConfig {
  maxNameLength: number;
  maxPhoneLength: number;
  maxContentLength: number;
  isInitialized: boolean;
}

const initialState: SystemConfig = {
  maxNameLength: 200,
  maxPhoneLength: 80,
  maxContentLength: 4000,
  isInitialized: false,
};

export const ConfigStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, http = inject(HttpClient), platformId = inject(PLATFORM_ID)) => ({
    async initializeApp() {
      if (store.isInitialized()) {
        return;
      }

      if (!isPlatformBrowser(platformId)) {
        patchState(store, { isInitialized: true });
        return;
      }

      if (environment.devMode) {
        patchState(store, { isInitialized: true });
        return;
      }

      try {
        const config = await firstValueFrom(http.get<SystemConfig>(`${environment.configUrl}`));

        patchState(store, {
          ...config,
          isInitialized: true,
        });
      } catch (err) {
        patchState(store, { isInitialized: false });
      }
    },
  })),
);
