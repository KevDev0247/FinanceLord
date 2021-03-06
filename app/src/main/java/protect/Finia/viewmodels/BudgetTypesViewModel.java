package protect.Finia.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import protect.Finia.models.BudgetsType;

/**
 * The view model to store and manage budget types and share them between fragments, classes, and activities.
 * It will monitor the change in the data stored and push or retrieve new data from the view model.
 * The data will be stored in Mutable Live Data, a data holder class that can be observed within a given lifecycle
 *
 * @author Owner  Kevin Zhijun Wang
 * created on 2020/05/30
 */
public class BudgetTypesViewModel extends ViewModel {
    private MutableLiveData<List<BudgetsType>> categoryLabels = new MutableLiveData<>();

    public void pushToBudgetTypes(List<BudgetsType> newBudgetTypes) {
        categoryLabels.setValue(newBudgetTypes);
    }

    public MutableLiveData<List<BudgetsType>> getCategoryLabels() {
        return categoryLabels;
    }
}
