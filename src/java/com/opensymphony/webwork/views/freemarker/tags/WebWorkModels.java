package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides @ww.xxx access for various tags.
 *
 * @author plightbo
 */
public class WebWorkModels {
    protected OgnlValueStack stack;
    protected HttpServletRequest req;
    protected HttpServletResponse res;

    protected ActionModel action;
    protected BeanModel bean;
    protected CheckboxModel checkbox;
    protected CheckboxListModel checkboxlist;
    protected ComboBoxModel comboBox;
    protected ComponentModel component;
    protected DateModel date;
    protected DatePickerModel datepicker;
    protected DivModel div;
    protected DebugModel debug;
    protected DoubleSelectModel doubleselect;
    protected FileModel file;
    protected FormModel form;
    protected HeadModel head;
    protected HiddenModel hidden;
    protected AnchorModel a;
    protected I18nModel i18n;
    protected IncludeModel include;
    protected LabelModel label;
    protected PanelModel panel;
    protected PasswordModel password;
    protected PushModel push;
    protected ParamModel param;
    protected RadioModel radio;
    protected SelectModel select;
    protected SetModel set;
    protected SubmitModel submit;
    protected ResetModel reset;
    protected TabbedPanelModel tabbedPanel;
    protected TextAreaModel textarea;
    protected TextModel text;
    protected TextFieldModel textfield;
    protected TokenModel token;
    protected URLModel url;
    protected WebTableModel table;
    protected PropertyModel property;
    protected IteratorModel iterator;
    protected ActionErrorModel actionerror;
    protected ActionMessageModel actionmessage;
    protected FieldErrorModel fielderror;
    protected OptionTransferSelectModel optiontransferselect;
    protected TreeModel treeModel;
    protected UpDownSelectModel updownselect;
    protected RichTextEditorModel richtexteditorModel;
    

    public WebWorkModels(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    public CheckboxListModel getCheckboxlist() {
        if (checkboxlist == null) {
            checkboxlist = new CheckboxListModel(stack, req, res);
        }

        return checkboxlist;
    }

    public CheckboxModel getCheckbox() {
        if (checkbox == null) {
            checkbox = new CheckboxModel(stack, req, res);
        }

        return checkbox;
    }

    public ComboBoxModel getComboBox() {
        if (comboBox == null) {
            comboBox = new ComboBoxModel(stack, req, res);
        }

        return comboBox;
    }

    public ComponentModel getComponent() {
        if (component == null) {
            component = new ComponentModel(stack, req, res);
        }

        return component;
    }

    public DoubleSelectModel getDoubleselect() {
        if (doubleselect == null) {
            doubleselect = new DoubleSelectModel(stack, req, res);
        }

        return doubleselect;
    }

    public FileModel getFile() {
        if (file == null) {
            file = new FileModel(stack, req, res);
        }

        return file;
    }

    public DebugModel getDebug() {
        if (debug == null) {
            debug = new DebugModel(stack, req, res);
        }

        return debug;
    }

    public FormModel getForm() {
        if (form == null) {
            form = new FormModel(stack, req, res);
        }

        return form;
    }

    public HeadModel getHead() {
        if (head == null) {
            head = new HeadModel(stack, req, res);
        }

        return head;
    }

    public HiddenModel getHidden() {
        if (hidden == null) {
            hidden = new HiddenModel(stack, req, res);
        }

        return hidden;
    }
    public LabelModel getLabel() {
        if (label == null) {
            label = new LabelModel(stack, req, res);
        }

        return label;
    }

    public PasswordModel getPassword() {
        if (password == null) {
            password = new PasswordModel(stack, req, res);
        }

        return password;
    }

    public RadioModel getRadio() {
        if (radio == null) {
            radio = new RadioModel(stack, req, res);
        }

        return radio;
    }

    public SelectModel getSelect() {
        if (select == null) {
            select = new SelectModel(stack, req, res);
        }

        return select;
    }

    public SubmitModel getSubmit() {
        if (submit == null) {
            submit = new SubmitModel(stack, req, res);
        }

        return submit;
    }

    public ResetModel getReset() {
        if (reset == null) {
            reset = new ResetModel(stack, req, res);
        }

        return reset;
    }

    public TextAreaModel getTextarea() {
        if (textarea == null) {
            textarea = new TextAreaModel(stack, req, res);
        }

        return textarea;
    }

    public TextFieldModel getTextfield() {
        if (textfield == null) {
            textfield = new TextFieldModel(stack, req, res);
        }

        return textfield;
    }

    public DateModel getDate() {
        if (date == null) {
            date = new DateModel(stack, req, res);
        }

        return date;
    }

    public DatePickerModel getDatepicker() {
        if (datepicker == null) {
            datepicker = new DatePickerModel(stack, req, res);
        }

        return datepicker;
    }

    public TokenModel getToken() {
        if (token == null) {
            token = new TokenModel(stack, req, res);
        }

        return token;
    }

    public WebTableModel getTable() {
        if (table == null) {
            table = new WebTableModel(stack, req, res);
        }

        return table;
    }

    public URLModel getUrl() {
        if (url == null) {
            url = new URLModel(stack, req, res);
        }

        return url;
    }

    public IncludeModel getInclude() {
        if (include == null) {
            include = new IncludeModel(stack, req, res);
        }

        return include;
    }

    public ParamModel getParam() {
        if (param == null) {
            param = new ParamModel(stack, req, res);
        }

        return param;
    }

    public ActionModel getAction() {
        if (action == null) {
            action = new ActionModel(stack, req, res);
        }

        return action;
    }

    public AnchorModel getA() {
        if (a == null) {
            a = new AnchorModel(stack, req, res);
        }

        return a;
    }

    public AnchorModel getHref() {
        if (a == null) {
            a = new AnchorModel(stack, req, res);
        }

        return a;
    }

    public DivModel getDiv() {
        if (div == null) {
            div = new DivModel(stack, req, res);
        }

        return div;
    }

    public TextModel getText() {
        if (text == null) {
            text = new TextModel(stack, req, res);
        }

        return text;
    }

    public TabbedPanelModel getTabbedPanel() {
        if (tabbedPanel == null) {
            tabbedPanel = new TabbedPanelModel(stack, req, res);
        }

        return tabbedPanel;
    }

    public PanelModel getPanel() {
        if (panel == null) {
            panel = new PanelModel(stack, req, res);
        }

        return panel;
    }

    public BeanModel getBean() {
        if (bean == null) {
            bean = new BeanModel(stack, req, res);
        }

        return bean;
    }

    public I18nModel getI18n() {
        if (i18n == null) {
            i18n = new I18nModel(stack, req, res);
        }

        return i18n;
    }

    public PushModel getPush() {
        if (push == null) {
            push = new PushModel(stack, req, res);
        }

        return push;
    }

    public SetModel getSet() {
        if (set == null) {
            set = new SetModel(stack, req, res);
        }

        return set;
    }

    public PropertyModel getProperty() {
        if (property == null) {
            property = new PropertyModel(stack, req, res);
        }

        return property;
    }

    public IteratorModel getIterator() {
        if (iterator == null) {
            iterator = new IteratorModel(stack, req, res);
        }

        return iterator;
    }

    public ActionErrorModel getActionerror() {
        if (actionerror == null) {
            actionerror = new ActionErrorModel(stack, req, res);
        }

        return actionerror;
    }

    public ActionMessageModel getActionmessage() {
        if (actionmessage == null) {
            actionmessage = new ActionMessageModel(stack, req, res);
        }

        return actionmessage;
    }

    public FieldErrorModel getFielderror() {
        if (fielderror == null) {
            fielderror = new FieldErrorModel(stack, req, res);
        }

        return fielderror;
    }
    
    public OptionTransferSelectModel getOptiontransferselect() {
    	if (optiontransferselect == null) {
    		optiontransferselect = new OptionTransferSelectModel(stack, req, res);
    	}
    	return optiontransferselect;
    }

    public TreeModel getTree() {
        if (treeModel == null) {
            treeModel = new TreeModel(stack,req, res);
        }
        return treeModel;
    }
    
    public UpDownSelectModel getUpdownselect() {
    	if (updownselect == null)  {
    		updownselect = new UpDownSelectModel(stack, req, res);
    	}
    	return updownselect;
    }
    
    public RichTextEditorModel getRichtexteditor() {
    	if (richtexteditorModel == null) {
    		richtexteditorModel = new RichTextEditorModel(stack, req, res);
    	}
    	return richtexteditorModel;
    }
}
