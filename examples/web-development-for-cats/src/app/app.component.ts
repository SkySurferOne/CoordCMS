import {Component, OnInit} from '@angular/core';
import {CoordCmsService} from "./coord-cms.service";

interface Field {}

const FieldType = {
  image: "Image",
  heading: "Heading",
  paragraph: "Paragraph"
};

export class Image implements Field {
  url: string;
  description: string;

  constructor(url: string, description: string) {
    this.url = url;
    this.description = description;
  }
}

export class Heading implements Field {
  content: string;

  constructor(content: string) {
    this.content = content;
  }
}

export class Paragraph implements Field {
  content: string;

  constructor(content: string) {
    this.content = content;
  }
}

export class Section {
  title: string;
  images: Image[];
  headings: Heading[];
  paragraphs: Paragraph[];

  constructor(title: string, images: Image[], headings: Heading[], paragraphs: Paragraph[]) {
    this.title = title;
    this.images = images;
    this.headings = headings;
    this.paragraphs = paragraphs;
  }
}

@Component({
  selector: 'my-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  headerData: Section;
  aboutData: Section;
  priceData: Section;
  contactData: Section;

  constructor(private coordCmsService: CoordCmsService) {
  }

  ngOnInit(): void {
    this.getSections();
  }

  getSections(): void {
    this.coordCmsService
      .getSections()
      .then(objects => {
        console.log(objects);

        let sections = objects.map(obj => {
          let [images, headings, paragraphs] = this.filterFields(obj['fields']);
          return new Section(obj['title'], images as Image[], headings as Heading[], paragraphs as Paragraph[]);
        });

        console.log(sections);
        [this.headerData, this.aboutData, this.priceData, this.contactData] = sections;
      });
  }

  private filterFields(fields: Array<Object>): Array<Array<Field>> {
    let images: Image[] = [],
      paragraphs: Paragraph[] = [],
      headings: Heading[] = [];

    fields.forEach((obj) => {
      switch(obj['fieldtype']) {
        case FieldType.heading:
          headings.push(new Heading(obj['content']));
          break;
        case FieldType.paragraph:
          paragraphs.push(new Paragraph(obj['content']));
          break;
        case FieldType.image:
          images.push(new Image(obj['url'], obj['description']));
          break;
      }
    });
    return [images, headings, paragraphs];
  }
}
