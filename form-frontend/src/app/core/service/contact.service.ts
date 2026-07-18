import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { ContactRequest, ContactResponse } from '../model/contact.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ContactService {
  private readonly apiUrl = `${environment.apiUrl}/contact`;
  private http = inject(HttpClient);

  findById(id: string): Observable<ContactResponse> {
    return this.http.get<ContactResponse>(`${this.apiUrl}/${id}`, {
      withCredentials: true,
    });
  }

  createContact(request: ContactRequest): Observable<ContactResponse> {
    return this.http.post<ContactResponse>(`${this.apiUrl}`, request, {
      withCredentials: true,
    });
  }

  deleteContact(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      withCredentials: true,
    });
  }
}
