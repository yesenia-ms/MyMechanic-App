package cs3773.group11.mymechanic.ui.garage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GarageViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GarageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is garage fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}