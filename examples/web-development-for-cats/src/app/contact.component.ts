import {Component, Input} from '@angular/core';
import {Section} from "./app.component";

@Component({
  selector: 'my-contact',
  templateUrl: './contact.component.html'
})
export class ContactComponent {
  @Input() data: Section
}
