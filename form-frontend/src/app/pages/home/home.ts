import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, effect, inject, OnInit, PLATFORM_ID, signal, untracked } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { ContactStore } from '../../core/store/contact.store';
import { ContactRequest } from '../../core/model/contact.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, FormField],
  templateUrl: './home.html',
  host: {
    class: 'flex-1 flex items-center justify-center p-4 w-full',
  },
})
export class Home implements OnInit {
  readonly store = inject(ContactStore);
  private platformId = inject(PLATFORM_ID);

  cooldown = signal<number>(0);
  showSuccess = signal<boolean>(false);

  private lastId = '';

  mailModel = signal<ContactRequest>({
    name: '',
    phone: '',
    content: '',
  });

  mailForm = form(this.mailModel, (f) => {
    required(f.name, { message: 'Your name is required.' });
    required(f.phone, { message: 'Your Phone number is required' });
    required(f.content, { message: 'Writing something is required' });
  });

  ngOnInit() {
    this.resumeCooldownIfActive();
  }

  private statusEffect = effect(() => {
    const contact = this.store.contact();

    if (
      contact?.status === 'SENT' &&
      contact.id !== this.lastId &&
      untracked(this.cooldown) === 0
    ) {
      this.lastId = contact.id;

      this.showSuccess.set(true);
      this.mailModel.set({ name: '', phone: '', content: '' });
      this.startCooldown();
    }
  });

  startCooldown(seconds: number = 30) {
    this.cooldown.set(seconds);

    if (isPlatformBrowser(this.platformId)) {
      const expirationTime = Date.now() + seconds * 1000;
      localStorage.setItem('contactCooldownEnd', expirationTime.toString());
    }

    this.runTimer();
  }

  resumeCooldownIfActive() {
    if (isPlatformBrowser(this.platformId)) {
      const savedExpiration = localStorage.getItem('contactCooldownEnd');

      if (savedExpiration) {
        const remainingMilliseconds = parseInt(savedExpiration, 10) - Date.now();

        if (remainingMilliseconds > 0) {
          const remainingSeconds = Math.ceil(remainingMilliseconds / 1000);
          this.cooldown.set(remainingSeconds);
          this.runTimer();
        } else {
          localStorage.removeItem('contactCooldownEnd');
        }
      }
    }
  }
  private runTimer() {
    const interval = setInterval(() => {
      if (this.cooldown() > 0) {
        this.cooldown.update((c) => c - 1);
      } else {
        clearInterval(interval);
        this.showSuccess.set(false);
        localStorage.removeItem('contactCooldownEnd');
        if (isPlatformBrowser(this.platformId)) {
          localStorage.removeItem('contactCooldownEnd');
        }
      }
    }, 1000);
  }

  onSubmit(event: Event): void {
    event.preventDefault();

    if (this.mailForm().invalid() || this.store.isLoading() || this.cooldown() > 0) {
      return;
    }

    const request: ContactRequest = this.mailModel();
    this.store.createContact(request); // create and send mail
  }
}
