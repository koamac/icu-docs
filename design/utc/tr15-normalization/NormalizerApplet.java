import java.awt.*;
import java.applet.*;

/**
 * Applet containing a single button that opens the Normalizer demo.
 * Copyright (c) 1991-2005 Unicode, Inc.
 * For terms of use, see http://www.unicode.org/terms_of_use.html
 * For documentation, see UAX#15.<br>
 * @author Mark Davis
 */
public class NormalizerApplet extends Applet {
    static final String copyright = "Copyright � 1998-1999 Unicode, Inc.";

    public void init() {
        super.init();
        setBackground(Color.white);
        add(button = new Button("     Test     "));
		button.addActionListener(new OpenAction());
    }

	class OpenAction implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent event) {
            String old = button.getLabel();
    		button.setLabel("Loading");
            Frame frame = new NormalizerFrame();
			frame.setBounds(30, 30, 530, 430);
			frame.show();
	 		button.setLabel(old);
        }
    }

    Button button;
}

/**
 * Frame containing the buttons and text fields for the demo.
 * @author Mark Davis
 */
class NormalizerFrame extends Frame {
    static final String copyright = "Copyright � 1998-1999 Unicode, Inc.";
    
    static final boolean DEBUG = false;
    static String testString = 
        "`a^a`a^`a`^a�^a�`a�^`a�`^c�\n"
        + "A~ qu\"i�c�k bro`wn fo^x ju\"mpe^d\n"
        + " !\"#$%&'()*+,-./0123456789:;<=>?\n"
        + "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_\n"
        + "`abcdefghijklmnopqrstuvwxyz{|}~\n"
        + "��������������������������������\n"
        + "��������������������������������\n"
        + "��������������������������������";
        
	public NormalizerFrame() {
		super("Normalizer Demo");
		init();
    }
    
	public void init() {
		setLayout(new GridLayout(2,1));
		Panel top = new Panel();
		Panel bottom = new Panel();
		add("North", top);
		add("South", bottom);
		
		top.setLayout(new BorderLayout());
		bottom.setLayout(new BorderLayout());
		
		Panel topButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
		Panel bottomButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		top.add("North", topButtons);
		top.add("Center", textArea1);
		bottom.add("North", bottomButtons);
		bottom.add("Center", textArea2);
		
		Font mono = new Font("Monospaced", Font.PLAIN, 12);
		textArea1.setText(testString);
		textArea1.setFont(mono);
		textArea2.setFont(mono);
				
		topButtons.add(new Label("Insert:"));
		addInsertButton("grave: `", topButtons);
		addInsertButton("acute: �", topButtons);
		addInsertButton("circum: ^", topButtons);
		addInsertButton("tilde: ~", topButtons);
		addInsertButton("umlaut: \"", topButtons);
		addInsertButton("ring: �", topButtons);
		addInsertButton("cedilla: �", topButtons);
		
	    bottomButtons.add(new Label("Normalize:"));
		addFormButton("Form D", new Normalizer(Normalizer.D, false), bottomButtons);
		addFormButton("Form C", new Normalizer(Normalizer.C, false), bottomButtons);
		addFormButton("Form KD", new Normalizer(Normalizer.KD, false), bottomButtons);
		addFormButton("Form KC", new Normalizer(Normalizer.KC, false), bottomButtons);
		
		addWindowListener(new SymAction2());
	}
	
	InsertAction insertAction = new InsertAction();
	
	/**
	 * Adds a special button for inserting substitutes
	 */
	void addInsertButton(String label, Container container) {
		Button temp = new Button(label);
		temp.addActionListener(insertAction);
		container.add(temp);
	}
	
	/**
	 * Adds a special button for doing forms
	 */
	void addFormButton(String label, Normalizer form, Container container) {
		Button temp = new Button(label);
		temp.addActionListener(new SymAction(form));
		container.add(temp);
	}
	
	// DECLARE_CONTROLS
	java.awt.TextArea textArea1 = new java.awt.TextArea();
	java.awt.TextArea textArea2 = new java.awt.TextArea();

	// Action stuff replacing switch code originally generated by Cafe
	
	class SymAction2 extends java.awt.event.WindowAdapter {
	    public void windowClosing(java.awt.event.WindowEvent event) {
	        if (DEBUG) System.out.println(event);
	        NormalizerFrame.this.setVisible(false);
		    NormalizerFrame.this.dispose();
	    }
	}

	class SymAction implements java.awt.event.ActionListener {
	    SymAction(Normalizer form) {
	        this.form = form;
	    }
		public void actionPerformed(java.awt.event.ActionEvent event) {
		    if (DEBUG) System.out.println("Action: " + event);
		    String norm = form.normalize(toCombining(textArea1.getText()));
		    textArea2.setText(fromCombining(norm));
		}
	    private Normalizer form;
	}

	class InsertAction implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent event) {
	        Button b = (Button)(event.getSource());
	        String label = b.getLabel();
            textArea1.replaceRange(label.substring(label.length()-1,label.length()),
                textArea1.getSelectionStart(), textArea1.getSelectionEnd());
            textArea1.requestFocus();
		}
	}

	char[] substitutes = {
	    '`', '\u0300',
	    '�', '\u0301',
	    '^', '\u0302',
	    '~', '\u0303',
	    '"', '\u0308',
	    '�', '\u030A',
	    '�', '\u0327',
	};
	
	String substitute(String source, char[] substitutePairs, boolean forward) {
	    StringBuffer result = new StringBuffer();
	    int testOffset = forward ? 0 : 1;
	    int replaceOffset = 1 - testOffset;
	    for (int i = 0; i < source.length(); ++i) {
	        char c = source.charAt(i);
	        for (int j = 0; j < substitutes.length; j+=2) {
	            if (c == substitutes[j + testOffset]) {
	                c = substitutes[j + replaceOffset];
	            }
	        }
	        result.append(c);
	    }
	    return result.toString();
	}

	// dumb conversion; don't worry about efficiency
	String toCombining(String source) {
	    return substitute(source, substitutes, true);
	}
	
	// dumb conversion; don't worry about efficiency
	String fromCombining(String source) {
	    return substitute(source, substitutes, false);
	}
}
