import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'error-not-found',
  imports: [RouterLink],
  templateUrl: './not-found.html',
  styleUrl: './not-found.scss',
})
export default class NotFound {
}
