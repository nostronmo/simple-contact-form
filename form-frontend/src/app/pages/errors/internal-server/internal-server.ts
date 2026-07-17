import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'error-internal-server',
  imports: [RouterLink],
  templateUrl: './internal-server.html',
  styleUrl: './internal-server.scss',
})
export default class InternalError {
}
