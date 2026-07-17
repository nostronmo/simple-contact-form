import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'error-not-authorized',
  imports: [RouterLink],
  templateUrl: './nonauthorized.html',
  styleUrl: './nonauthorized.scss',
})
export default class NotAuthorized {
}
