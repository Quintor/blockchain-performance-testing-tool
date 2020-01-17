package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class HeaderTextComponent extends TextFieldBuilder {

    public HeaderTextComponent(String text) {
        setText(text);
        setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        setStyle(stl.style().setFont(stl.font().bold().setFontSize(20)));
    }
}
