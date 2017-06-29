import {Component, Input} from '@angular/core';
import {Section} from "./app.component";

@Component({
  selector: 'my-header',
  templateUrl: './header.component.html'
})
export class HeaderComponent {
  @Input() data: Section
}
