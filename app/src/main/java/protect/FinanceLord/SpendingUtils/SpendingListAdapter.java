package protect.FinanceLord.SpendingUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import protect.FinanceLord.R;

public class SpendingListAdapter extends ArrayAdapter<MonthlyTotalSpending> {

    public SpendingListAdapter(@NonNull Context context, List<MonthlyTotalSpending> monthlyTotalSpendingList) {
        super(context,0, monthlyTotalSpendingList);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MonthlyTotalSpending currentTotalSpending = getItem(position);
        MonthlyTotalSpending previousTotalSpending = null;
        float difference = 0;
        if (position != 0) {
            previousTotalSpending = getItem(position - 1);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spending_item, null, false);
        }

        TextView month = convertView.findViewById(R.id.spending_time);
        TextView spendingValue = convertView.findViewById(R.id.spending_total_value);
        TextView spendingDifference = convertView.findViewById(R.id.spending_difference);
        TextView spendingDifferenceSymbol = convertView.findViewById(R.id.spending_difference_symbol);
        LinearLayout differenceBlockView =  convertView.findViewById(R.id.spending_item_difference_block);

        month.setText(currentTotalSpending.month);
        spendingValue.setText(String.valueOf(currentTotalSpending.categoryTotal));
        spendingDifference.setText(getContext().getString(R.string.no_data_message));
        if (previousTotalSpending != null) {
            difference = currentTotalSpending.categoryTotal - previousTotalSpending.categoryTotal;
            spendingDifference.setText(String.valueOf(difference));
        }

        if (difference == 0) {
            spendingDifferenceSymbol.setText("");
            differenceBlockView.setBackgroundResource(R.drawable.net_neutral);
        } else if (difference > 0) {
            spendingDifferenceSymbol.setText("");
            differenceBlockView.setBackgroundResource(R.drawable.net_decrease);
        } else if (difference < 0) {
            spendingDifferenceSymbol.setText("");
            differenceBlockView.setBackgroundResource(R.drawable.net_increase);
        }

        return convertView;
    }
}
