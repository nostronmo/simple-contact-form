import { inject } from '@angular/core';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { ContactService } from '../service/contact.service';
import { pipe, tap, switchMap } from 'rxjs';
import { tapResponse } from '@ngrx/operators';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { ContactRequest, ContactResponse } from '../model/contact.model';
import { HttpErrorResponse } from '@angular/common/http';

interface ContactState {
  contact: ContactResponse | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: ContactState = {
  contact: null,
  isLoading: false,
  error: null,
};

export const ContactStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, contactService = inject(ContactService)) => ({
    loadContact: rxMethod<string>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          contactService.findById(id).pipe(
            tapResponse({
              next: (response) => patchState(store, { contact: response, isLoading: false }),
              error: (error) => {
                patchState(store, { isLoading: false, error: 'ERROR' });
              },
            }),
          ),
        ),
      ),
    ),

    createContact: rxMethod<ContactRequest>(
      pipe(
        tap(() => patchState(store, { isLoading: true })),
        switchMap((request) =>
          contactService.createContact(request).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, { contact: response });
              },
              error: (err: HttpErrorResponse) => {
                const backendError =
                  err.error?.errorCode || err.error?.message || err.error || 'ERROR';
                patchState(store, { isLoading: false, error: backendError });
              },
            }),
          ),
        ),
      ),
    ),
  })),
);
