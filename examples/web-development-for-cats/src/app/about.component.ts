import {Component, Input} from '@angular/core';
import {Section} from "./app.component";

@Component({
  selector: 'my-about',
  templateUrl: './about.component.html'
})
export class AboutComponent {
  @Input() data: Section
}
