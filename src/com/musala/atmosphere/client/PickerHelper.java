package com.musala.atmosphere.client;

import java.util.List;

import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.UiElementPropertiesContainer;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * Class for getting number pickers and setting text in their editText field.
 * 
 * @author yavor.stankov
 * 
 */

public class PickerHelper {
    private static final String NUMBER_PICKER_WIDGET = "android.widget.NumberPicker";

    private static final String TEXT_WIDGET = "android.widget.EditText";

    private Screen screen;

    public PickerHelper(Screen screen) {
        this.screen = screen;
    }

    /**
     * Gets the text from the edit text field in a number picker widget selected by the requested instance.
     * 
     * @param index
     *        - used for matching criterion when selecting number picker widget from the picker.
     * @return The text of the edit text field in a number picker widget.
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if multiple elements are found for the passed query
     */
    public String getNumberPickerFieldValue(int index)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        UiElement numberPickerField = getNumberPickerField(index);
        UiElementPropertiesContainer numberPickerFieldElementProperties = numberPickerField.getProperties();
        screen.updateScreen();
        return numberPickerFieldElementProperties.getText();
    }

    /**
     * Sets the text in the edit text field in a number picker widget selected by the requested instance.
     * 
     * @param pickerIndex
     *        - used for matching criterion when selecting number picker widget from the picker.
     * @param text
     *        - text to set in the edit text field in a number picker widget.
     * @return <code>true</code> if the method is successful, <code>false</code> if it fails.
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if multiple elements are found for the passed query
     */
    public boolean setTextInNumberPickerField(int pickerIndex, String text)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        UiElement numberPickerField = getNumberPickerField(pickerIndex);

        if (!numberPickerField.inputText(text)) {
            return false;
        }

        screen.updateScreen();

        return true;
    }

    /**
     * Gets the editText field of a picker with a given index.
     * 
     * @param index
     *        - the index of the picker, we are interested in.
     * @return The editText field of the given picker.
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if multiple elements are found for the passed query
     */
    public UiElement getNumberPickerField(int index) throws MultipleElementsFoundException, UiElementFetchingException {
        UiElement numberPicker = getNumberPicker(index);

        UiElementSelector textFieldSelector = new UiElementSelector();
        textFieldSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, TEXT_WIDGET);

        List<UiElement> children = numberPicker.getChildren(textFieldSelector);

        if (children.isEmpty()) {
            throw new UiElementFetchingException("Cannot retrieve number picker field.");
        }

        UiElement numberPickerField = children.get(0);

        return numberPickerField;
    }

    /**
     * Gets a picker with a given index.
     * 
     * @param index
     *        - the index of the picker.
     * @return The picker with the given index.
     * @throws UiElementFetchingException
     *         if picker for the passed query is not found
     * @throws MultipleElementsFoundException
     *         if more than one pickers are found for the passed query
     */
    public UiElement getNumberPicker(int index) throws MultipleElementsFoundException, UiElementFetchingException {
        UiElementSelector numberPickerSelector = new UiElementSelector();

        numberPickerSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, NUMBER_PICKER_WIDGET);
        numberPickerSelector.addSelectionAttribute(CssAttribute.INDEX, index);

        UiElement numberPicker = screen.getElement(numberPickerSelector);

        // We click on the picker to make it visible.
        numberPicker.tap();

        screen.updateScreen();

        UiElement numberPickerClicked = screen.getElement(numberPickerSelector);

        return numberPickerClicked;
    }
}
