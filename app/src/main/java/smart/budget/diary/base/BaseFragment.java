package smart.budget.diary.base;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

/**
 * BaseFragment is a parent class for fragments used in the EasyBudget project.
 * It provides common functionality related to Firebase authentication.
 */
public class BaseFragment extends Fragment {

    /**
     * Retrieves the unique user ID (UID) of the currently authenticated user.
     * @return The user ID (UID) of the current user.
     */
    public String getUid() {
        // Using FirebaseAuth.getInstance().getCurrentUser() to get the current user and then accessing its UID.
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
