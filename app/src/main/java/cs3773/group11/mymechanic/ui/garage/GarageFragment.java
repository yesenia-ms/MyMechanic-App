package cs3773.group11.mymechanic.ui.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cs3773.group11.mymechanic.databinding.FragmentGarageBinding;

public class GarageFragment extends Fragment {

    private FragmentGarageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GarageViewModel garageViewModel =
                new ViewModelProvider(this).get(GarageViewModel.class);

        binding = FragmentGarageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGarage;
        garageViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}