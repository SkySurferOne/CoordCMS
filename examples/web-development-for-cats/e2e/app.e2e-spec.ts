import { WebDevelopmentForCatsPage } from './app.po';

describe('web-development-for-cats App', () => {
  let page: WebDevelopmentForCatsPage;

  beforeEach(() => {
    page = new WebDevelopmentForCatsPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!!');
  });
});
