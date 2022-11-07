package com.example.application.views.modifyflashcards;

import com.example.application.data.entity.FlashCard;
import com.example.application.data.service.FlashCardService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("ModifyFlashCards")
@Route(value = "ModifyFlashCards/:flashCardID?/:action?(edit)", layout = MainLayout.class)
public class ModifyFlashCardsView extends Div implements BeforeEnterObserver {

    private final String FLASHCARD_ID = "flashCardID";
    private final String FLASHCARD_EDIT_ROUTE_TEMPLATE = "ModifyFlashCards/%s/edit";

    private final Grid<FlashCard> grid = new Grid<>(FlashCard.class, false);

    private TextField lessonNumber;
    private TextField cardNumber;
    private TextField frontText;
    private TextField backText;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<FlashCard> binder;

    private FlashCard flashCard;

    private final FlashCardService flashCardService;

    @Autowired
    public ModifyFlashCardsView(FlashCardService flashCardService) {
        this.flashCardService = flashCardService;
        addClassNames("modify-flash-cards-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("lessonNumber").setAutoWidth(true);
        grid.addColumn("cardNumber").setAutoWidth(true);
        grid.addColumn("frontText").setAutoWidth(true);
        grid.addColumn("backText").setAutoWidth(true);
        grid.setItems(query -> flashCardService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(FLASHCARD_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ModifyFlashCardsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(FlashCard.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(lessonNumber).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("lessonNumber");
        binder.forField(cardNumber).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("cardNumber");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.flashCard == null) {
                    this.flashCard = new FlashCard();
                }
                binder.writeBean(this.flashCard);
                flashCardService.update(this.flashCard);
                clearForm();
                refreshGrid();
                Notification.show("FlashCard details stored.");
                UI.getCurrent().navigate(ModifyFlashCardsView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the flashCard details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> flashCardId = event.getRouteParameters().get(FLASHCARD_ID).map(UUID::fromString);
        if (flashCardId.isPresent()) {
            Optional<FlashCard> flashCardFromBackend = flashCardService.get(flashCardId.get());
            if (flashCardFromBackend.isPresent()) {
                populateForm(flashCardFromBackend.get());
            } else {
                Notification.show(String.format("The requested flashCard was not found, ID = %s", flashCardId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ModifyFlashCardsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        lessonNumber = new TextField("Lesson Number");
        cardNumber = new TextField("Card Number");
        frontText = new TextField("Front Text");
        backText = new TextField("Back Text");
        formLayout.add(lessonNumber, cardNumber, frontText, backText);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(FlashCard value) {
        this.flashCard = value;
        binder.readBean(this.flashCard);

    }
}
