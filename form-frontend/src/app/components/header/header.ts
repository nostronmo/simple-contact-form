import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { HeaderActions } from '../header-actions/header-actions';

@Component({
  selector: 'app-header',
  imports: [CommonModule, HeaderActions],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {}
