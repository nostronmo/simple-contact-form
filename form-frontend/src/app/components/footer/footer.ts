import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FooterActions } from '../footer-actions/footer-actions';

@Component({
  selector: 'app-footer',
  imports: [CommonModule, FooterActions],
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
})
export class Footer {
  currentYear = new Date().getFullYear();
}
