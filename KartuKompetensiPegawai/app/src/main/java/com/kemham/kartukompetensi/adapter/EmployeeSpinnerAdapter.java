package com.kemham.kartukompetensi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kemham.kartukompetensi.R;
import com.kemham.kartukompetensi.model.Employee;
import com.kemham.kartukompetensi.view.InitialsAvatarView;

import java.util.List;

/**
 * Adapter for displaying employees in a spinner with custom layout.
 */
public class EmployeeSpinnerAdapter extends ArrayAdapter<Employee> {
    private final LayoutInflater inflater;
    private final List<Employee> employees;

    public EmployeeSpinnerAdapter(Context context, List<Employee> employees) {
        super(context, R.layout.item_employee_spinner_selected, employees);
        this.inflater = LayoutInflater.from(context);
        this.employees = employees;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent, R.layout.item_employee_spinner_dropdown);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent, R.layout.item_employee_spinner_selected);
    }

    private View createItemView(int position, View convertView, ViewGroup parent, int layoutId) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.avatarView = convertView.findViewById(R.id.avatar_view);
            holder.nameText = convertView.findViewById(R.id.text_name);
            holder.nipText = convertView.findViewById(R.id.text_nip);
            holder.unitText = convertView.findViewById(R.id.text_unit);
            holder.statusIndicator = convertView.findViewById(R.id.status_indicator);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Employee employee = employees.get(position);
        if (employee != null) {
            // Set employee name and generate initials for avatar
            holder.nameText.setText(employee.getName());
            if (holder.avatarView != null) {
                holder.avatarView.setInitials(employee.getName());
            }

            // Set NIP with proper formatting
            String formattedNip = formatNip(employee.getNip());
            holder.nipText.setText(formattedNip);

            // Set unit if available
            if (holder.unitText != null) {
                String unit = employee.getUnit();
                if (unit != null && !unit.isEmpty()) {
                    holder.unitText.setVisibility(View.VISIBLE);
                    holder.unitText.setText(unit);
                } else {
                    holder.unitText.setVisibility(View.GONE);
                }
            }

            // Set status indicator color
            if (holder.statusIndicator != null) {
                int statusColor = getStatusColor(employee.getStatus());
                holder.statusIndicator.setBackgroundColor(statusColor);
                holder.statusIndicator.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    private String formatNip(String nip) {
        if (nip == null || nip.length() != 18) {
            return nip;
        }
        // Format: 198501012010011001 -> 19850101 201001 1 001
        return nip.substring(0, 8) + " " + 
               nip.substring(8, 14) + " " + 
               nip.substring(14, 15) + " " + 
               nip.substring(15);
    }

    private int getStatusColor(Employee.EmployeeStatus status) {
        Context context = getContext();
        switch (status) {
            case ACTIVE:
                return context.getResources().getColor(R.color.status_active);
            case INACTIVE:
                return context.getResources().getColor(R.color.status_inactive);
            case ON_LEAVE:
                return context.getResources().getColor(R.color.status_on_leave);
            case TRANSFERRED:
                return context.getResources().getColor(R.color.status_transferred);
            default:
                return context.getResources().getColor(R.color.status_default);
        }
    }

    public void updateEmployees(List<Employee> newEmployees) {
        employees.clear();
        employees.addAll(newEmployees);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return employees.size();
    }

    @Nullable
    @Override
    public Employee getItem(int position) {
        return position >= 0 && position < employees.size() ? employees.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        Employee employee = getItem(position);
        return employee != null ? employee.getNip().hashCode() : 0;
    }

    private static class ViewHolder {
        InitialsAvatarView avatarView;
        TextView nameText;
        TextView nipText;
        TextView unitText;
        View statusIndicator;
    }
}
