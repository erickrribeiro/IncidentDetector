package erickribeiro.incidentdetector.costumizado;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import erickribeiro.incidentdetector.R;


public class MeuContatoPreference extends Preference {
    private int mClickCounter;
    private boolean tipo; //Sms ou Ligação

    public MeuContatoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.meucontato);
        setTipo(true);
    }

    public void setTipo(boolean tipo){
    	this.tipo = tipo;
    }
    
    private boolean getTipo() {
    	return tipo;
	}
    
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        if(getTipo()){
        	ImageView imageView = (ImageView) view.findViewById(R.id.meucontato_widget);
        	imageView.setImageResource(R.drawable.sms);
        }
    }

    @Override
    protected void onClick() {
        int newValue = mClickCounter + 1;
        // Give the client a chance to ignore this change if they deem it
        // invalid
        if (!callChangeListener(newValue)) {
            // They don't want the value to be set
            return;
        }
        
        // Increment counter
        mClickCounter = newValue;
        // Save to persistent storage (this method will make sure this
        // preference should be persistent, along with other useful checks)
        persistInt(mClickCounter);

        // Data has changed, notify so UI can be refreshed!
        notifyChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // This preference type's value type is Integer, so we read the default
        // value from the attributes as an Integer.
        return a.getInteger(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            // Restore state
            mClickCounter = getPersistedInt(mClickCounter);
        } else {
            // Set state
            int value = (Integer) defaultValue;
            mClickCounter = value;
            persistInt(value);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        /*
         * Suppose a client uses this preference type without persisting. We
         * must save the instance state so it is able to, for example, survive
         * orientation changes.
         */

        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        // Save the instance state
        final SavedState myState = new SavedState(superState);
        myState.clickCounter = mClickCounter;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the instance state
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        mClickCounter = myState.clickCounter;
        notifyChanged();
    }

    private static class SavedState extends BaseSavedState {
        int clickCounter;

        public SavedState(Parcel source) {
            super(source);

            // Restore the click counter
            clickCounter = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            // Save the click counter
            dest.writeInt(clickCounter);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

       
    }

}