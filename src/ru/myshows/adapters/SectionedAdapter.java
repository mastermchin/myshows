package ru.myshows.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.myshows.activity.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SectionedAdapter extends BaseAdapter implements Serializable {

    private LayoutInflater inflater;
    private List<Section> sections = new ArrayList<Section>();
    private static int TYPE_SECTION_HEADER = 0;
    private View.OnClickListener clickListener;

    public SectionedAdapter(LayoutInflater inflater, View.OnClickListener clickListener) {
        this.inflater = inflater;
        this.clickListener = clickListener;
    }

    public SectionedAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
        TextView result = (TextView) convertView;
        if (convertView == null) {
            result = (TextView) inflater.inflate(R.layout.header, null);
        }
        result.setText(caption);
        if (clickListener != null) {
            result.setOnClickListener(clickListener);
        }
        return (result);
    }


    public SectionedAdapter() {
        super();
    }

    public void addSection(String caption, Adapter adapter) {
        sections.add(new Section(caption, adapter));
    }

    public Object getItem(int position) {
        for (Section section : this.sections) {
            if (position == 0) {
                return (section);
            }

            int size = section.adapter.getCount() + 1;

            if (position < size) {
                return (section.adapter.getItem(position - 1));
            }

            position -= size;
        }

        return (null);
    }


    public Object getItem(int position, String section) {
        for (Section s : sections) {
            if (s.caption.equals(section))
                return s.adapter.getItem(position);
        }
        return (null);
    }


    public int getCount() {
        int total = 0;

        for (Section section : this.sections) {
            total += section.adapter.getCount() + 1; // add one for header
        }

        return (total);
    }

    public int getViewTypeCount() {
        int total = 1;    // one for the header, plus those from sections

        for (Section section : this.sections) {
            total += section.adapter.getViewTypeCount();
        }

        return (total);
    }

    public int getItemViewType(int position) {
        int typeOffset = TYPE_SECTION_HEADER + 1;    // start counting from here

        for (Section section : this.sections) {
            if (position == 0) {
                return (TYPE_SECTION_HEADER);
            }

            int size = section.adapter.getCount() + 1;

            if (position < size) {
                return (typeOffset + section.adapter.getItemViewType(position - 1));
            }

            position -= size;
            typeOffset += section.adapter.getViewTypeCount();
        }

        return (-1);
    }

    public boolean areAllItemsSelectable() {
        return (false);
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionIndex = 0;

        for (Section section : this.sections) {
            if (position == 0) {
                return (getHeaderView(section.caption, sectionIndex, convertView, parent));
            }

            int size = section.adapter.getCount() + 1;

            if (position < size) {
                return (section.adapter.getView(position - 1,
                        convertView,
                        parent));
            }

            position -= size;
            sectionIndex++;
        }

        return (null);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    public class Section {
       public String caption;
       public Adapter adapter;

        Section(String caption, Adapter adapter) {
            this.caption = caption;
            this.adapter = adapter;
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Section getSection(String caption){
        for(Section s: sections){
            if(s.caption.equals(caption)) return s;
        }
        return null;
    }

    public void removeSection(String caption){
        for(Section s: sections){
            if(s.caption.equals(caption)) sections.remove(s);
        }
    }





}