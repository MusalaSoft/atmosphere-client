package com.musala.atmosphere.client;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
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
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements or more than one are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     */
    public String getNumberPickerFieldValue(int index)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException {
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
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements or more than one are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     */
    public boolean setTextInNumberPickerField(int pickerIndex, String text)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException {
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
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements or more than one are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     */
    public UiElement getNumberPickerField(int index)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException {
        UiElement numberPicker = getNumberPicker(index);

        UiElementSelector textFieldSelector = new UiElementSelector();
        textFieldSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, TEXT_WIDGET);

        UiElement numberPickerField = numberPicker.getChildrenByCssQuery(textFieldSelector.buildCssQuery()).get(0);

        return numberPickerField;
    }

    /**
     * Gets a picker with a given index.
     * 
     * @param index
     *        - the index of the picker.
     * @return The picker with the given index.
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements or more than one are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     */
    public UiElement getNumberPicker(int index)
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException {
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
