package org.openstreetmap.OSMZmiany;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;

public class NameDialog extends JDialog {
	interface NameDialogListener{
		public void okPressed(NameDialog nd);
	}
	
	private static final long serialVersionUID = 4028343189228849431L;
	private final JPanel contentPanel = new JPanel();
	private JTextField tFName;

	
	/**
	 * Create the dialog.
	 */
	public NameDialog(String text, NameDialogListener ndl) {		
		setBounds(100, 100, 309, 94);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
		{
			JLabel lblName = new JLabel(text);
			contentPanel.add(lblName);
		}
		{
			tFName = new JTextField();
			contentPanel.add(tFName);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");				
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	public String getText(){
		return tFName.getText();
	}

}
