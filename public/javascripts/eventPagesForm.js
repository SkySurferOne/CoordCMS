(function($) {
    /**
     * Globals
     */
    var fieldType = {
        HEADING : 'Heading',
        PARAGRAPH : 'Paragraph',
        IMAGE : 'Image'
    };

    /**
     * View
     */
    var View = function() {};
    View.prototype.ind = function () {
        return this.compNum - 1;
    };

    var PageView = function(compNum) {
        this.compNum = compNum;
    };
    PageView.prototype = View.prototype;

    var SectionView = function(pageInd, compNum) {
        this.pageInd = pageInd;
        this.compNum = compNum;
    };
    SectionView.prototype = View.prototype;

    var FieldView = function(pageInd, sectionInd, compNum, type) {
        this.pageInd = pageInd;
        this.sectionInd = sectionInd;
        this.compNum = compNum;
        this.type = type;
    };
    FieldView.prototype = View.prototype;

    /**
     * On document ready
     */
    $(document).ready(function() {
        // cache
        var $pages = $('.pages');

        // init
        var tpl = loadTemplates();

        if(isEmpty($pages)) {
            var output = Mustache.render(tpl.page, new PageView(1), tpl.pagePart);
            $pages.append(output);
        }

        // events
        initEvents({ pages: $pages }, tpl);
    });

    /**
     * Other
     */
    function isEmpty( el ){
        return !$.trim(el.html())
    }

    function hideUnnecessaryFields($field, type) {
        switch (type) {
            case fieldType.HEADING:
            case fieldType.PARAGRAPH:
                $field.find('.field-header').removeClass('hidden');
                $field.find('.field-image').addClass('hidden');
                break;
            case fieldType.IMAGE:
                $field.find('.field-header').addClass('hidden');
                $field.find('.field-image').removeClass('hidden');
                break;
        }
    }

    function initEvents($dom, tpl) {
        $dom.pages.on('click', '.add-section-btn', function(e) {
            e.preventDefault();

            var $sections = $(e.target).parent().find('.sections');
            var pageInd = $sections.parent().parent().index();
            var sectionNum = $sections.children().length + 1;
            var output = Mustache.render(tpl.section, new SectionView(pageInd, sectionNum), tpl.sectionPart);

            $sections.append(output);
        });

        $dom.pages.on('click', '.add-field', function(e) {
            e.preventDefault();

            var $fields = $(this).parent().parent().parent().parent().find('.fields');
            var fieldType = $(this).attr('data-type');
            var $section = $fields.parent().parent();
            var sectionInd = $section.index();
            var pageIng = $section.parent().parent().parent().index();
            var fieldNum = $fields.children().length + 1;

            var output = Mustache.render(tpl.field, new FieldView(pageIng, sectionInd, fieldNum, fieldType), tpl.fieldPart);
            var $output = $(output);

            hideUnnecessaryFields($output, fieldType);
            $fields.append($output);
        });

        $dom.pages.on('click', '.btn-panel-field-up, .btn-panel-section-up', function(e) {
            var $btn = $(this);
            var $child = $btn.parent().parent();
            var $body = $child.find('> .panel-body ');
            $btn.find('span').toggleClass('hidden');
            $body.slideToggle();
        });

        $dom.pages.on('click', '.btn-panel-field-remove, .btn-panel-section-remove', function(e) {
            var $compToDelete = $(e.currentTarget).parent().parent();
            var compInd = $compToDelete.index();
            var $siblings = $compToDelete.find('~ *');

            $siblings.each(function(i, element) {
                var $element = $(element);

                if ($element.hasClass('section')) {
                    fixSectionEnumeration($element, compInd + i);
                } else if($element.hasClass('field')) {
                    fixFieldEnumeration($element, compInd + i);
                }
            });

            $compToDelete.remove();
        });
    }

    function fixFieldEnumeration($field, ind) {
        var compNum = ind + 1;

        $field.find('[for*=pages_]').each(function(i, element) {
            var forAttr = element.getAttribute('for').split('_');
            forAttr[5] = ind.toString();
            forAttr = forAttr.join('_');
            element.setAttribute('for', forAttr);
        });

        $field.find('[id*=pages_]').each(function(i, element) {
            var idAttr = element.getAttribute('id').split('_');
            idAttr[5] = ind.toString();
            idAttr = idAttr.join('_');

            var nameAttr = element.getAttribute('name').split('.');
            nameAttr[2] = 'fields['+ind.toString()+']';
            if(nameAttr[3] === 'ordinal') {
                element.setAttribute('value', compNum);
            }
            nameAttr = nameAttr.join('.');

            element.setAttribute('id', idAttr);
            element.setAttribute('name', nameAttr);
        });
    }

    function fixSectionEnumeration($section, ind) {
        var compNum = ind + 1;

        $section.find('[for*=pages_]').each(function(i, element) {
            var forAttr = element.getAttribute('for').split('_');
            forAttr[3] = ind.toString();
            forAttr = forAttr.join('_');
            element.setAttribute('for', forAttr);
        });

        $section.find('[id*=pages_]').each(function(i, element) {
            var idAttr = element.getAttribute('id').split('_');
            idAttr[3] = ind.toString();
            idAttr = idAttr.join('_');

            var nameAttr = element.getAttribute('name').split('.');
            nameAttr[1] = 'sections['+ind.toString()+']';
            if(nameAttr[2] === 'ordinal') {
                element.setAttribute('value', compNum);
            }
            nameAttr = nameAttr.join('.');

            console.log(idAttr, nameAttr);

            element.setAttribute('id', idAttr);
            element.setAttribute('name', nameAttr);
        });
        $section.find('> .panel-heading .header').html('Section ' + compNum);
    }

    function loadTemplates() {
        var pageTpl = $('#page-template').html();
        var pageTplId = $('#page-template-id').html();
        var pageTplName = $('#page-template-name').html();

        var sectionTpl = $('#section-template').html();
        var sectionTplId = $('#section-template-id').html();
        var sectionTplName = $('#section-template-name').html();

        var fieldTpl = $('#field-template').html();
        var fieldTplId = $('#field-template-id').html();
        var fieldTplName = $('#field-template-name').html();

        Mustache.parse(pageTpl);
        Mustache.parse(pageTplId);
        Mustache.parse(pageTplName);

        Mustache.parse(sectionTpl);
        Mustache.parse(sectionTplId);
        Mustache.parse(sectionTplName);

        Mustache.parse(fieldTpl);
        Mustache.parse(fieldTplId);
        Mustache.parse(fieldTplName);

        return {
            page: pageTpl,
            pagePart: { id: pageTplId, name: pageTplName },
            section: sectionTpl,
            sectionPart: { id: sectionTplId, name: sectionTplName },
            field: fieldTpl,
            fieldPart: { id: fieldTplId, name: fieldTplName }
        };
    }

})(jQuery);
