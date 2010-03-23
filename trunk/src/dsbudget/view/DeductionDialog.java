package dsbudget.view;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import com.divrep.DivRepEvent;
import com.divrep.DivRepEventListener;
import com.divrep.common.DivRepDialog;
import com.divrep.common.DivRepTextBox;
import dsbudget.model.Deduction;
import dsbudget.model.Income;

public class DeductionDialog extends DivRepDialog
{
	MainView mainview;
	
	public DivRepTextBox amount;
	public DivRepTextBox description;
	
	Income income;
	Deduction deduction;
	NumberFormat nf = NumberFormat.getCurrencyInstance();
	
	public DeductionDialog(MainView parent) {
		super(parent);
		mainview = parent;
		
		setHeight(250);
		setWidth(450);
		
		description = new DivRepTextBox(this);
		description.setLabel("Description");
		description.setRequired(true);
		description.setWidth(300);
		
		amount = new DivRepMoneyAmount(this);
		amount.setLabel("Amount");
		amount.setWidth(200);
		amount.setSampleValue(nf.format(10));
		amount.setRequired(true);
		/*
		amount.addEventListener(new DivRepEventListener() {
			public void handleEvent(DivRepEvent e) {
				String value = e.value.trim();
				amount.setValue("");
				try {
					BigDecimal b = new BigDecimal(value);
					amount.setValue(nf.format(b));
				} catch(NumberFormatException ne) {
					try {
						Number n = nf.parse(value);
						amount.setValue(nf.format(n));
					} catch (ParseException e1) {
						//any other idea?
					}

				}
				amount.redraw();
			}
		});
		*/
	}
	
	public void open(Income _income, Deduction _deduction)
	{
		income = _income;
		deduction = _deduction;
		if(deduction == null) {
			setTitle("New Deduction for " + income.getName());
			description.setValue("");
			amount.setValue("");
		} else {
			setTitle("Update Deduction for " + income.getName());
			description.setValue(deduction.description);
			amount.setValue(nf.format(deduction.amount));
		}
		description.redraw();
		amount.redraw();
		super.open();
	}
	
	public void onCancel() {
		close();
	}
	public void onSubmit() {
		if(validate()) {
			if(deduction == null) {
				//new deduction
				deduction = new Deduction();
				income.deductions.add(deduction);
			}
			
			try {
				deduction.amount = new BigDecimal(nf.parse(amount.getValue()).toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			deduction.description = description.getValue();
			
			close();
			mainview.updateIncomeView();
			mainview.save();
		}
	}
	public void renderDialog(PrintWriter out) {
		description.render(out);
		amount.render(out);
	}
	protected Boolean validate()
	{
		Boolean valid = true;
		valid &= description.isValid();
		valid &= amount.isValid();
		return valid;
	}
};
