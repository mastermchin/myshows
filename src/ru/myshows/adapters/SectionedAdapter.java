package ru.myshows.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.myshows.activity.R;
import ru.myshows.domain.*;
import ru.myshows.domain.Filterable;
import ru.myshows.fragments.NewsFragment;
import ru.myshows.fragments.NextEpisodesFragment;
import ru.myshows.fragments.ShowsFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SectionedAdapter extends ArrayAdapter implements Serializable {

    private static int TYPE_SECTION_HEADER = 0;

    private Context context;
    private List<Section> sections;
    private List<Section> original;
    private Filter filter;

    public SectionedAdapter(Context context, int textViewResourceId, List objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.sections = (List<Section>) objects;
        if (objects != null)
            this.original = new ArrayList<Section>(objects);
    }


    protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
        TextView result = (TextView) convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = (TextView) inflater.inflate(R.layout.header, null);
        }
        result.setText(caption);
        return (result);
    }


    private class ShowsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                List<Section> founded = new ArrayList<Section>();
                for (Section s : original) {
                    ArrayAdapter<Filterable> arrayAdapter = (ArrayAdapter<Filterable>) s.adapter;
                    List foundedObjects = new ArrayList();
                    for (int i = 0; i < arrayAdapter.getCount(); i++) {
                        Filterable f = arrayAdapter.getItem(i);
                        if (f.getFilterString().toLowerCase().contains(constraint))
                            foundedObjects.add(f);
                    }

                    if (!foundedObjects.isEmpty()) {
                        // shows and search
                        if (s.adapter instanceof ShowsFragment.ShowsAdapter)
                            founded.add(new Section(s.caption, new ShowsFragment.ShowsAdapter(context, R.layout.show_item, (List<IShow>) foundedObjects)));
                        // news
                        if (s.adapter instanceof NewsFragment.NewsAdapter)
                            founded.add(new Section(s.caption, new NewsFragment.NewsAdapter(context, R.layout.show_item, (List<UserNews>) foundedObjects)));
                        // next episodes
                        if (s.adapter instanceof NextEpisodesFragment.EpisodesAdapter)
                            founded.add(new Section(s.caption, new NextEpisodesFragment.EpisodesAdapter(context, R.layout.show_item, (List<Episode>) foundedObjects)));

                    }

                }

                result.values = founded;
                result.count = founded.size();

            } else {
                result.values = original;
                result.count = original.size();
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            if (filterResults.values != null && filterResults.count > 0) {
                List<Section> sectionList = (List<Section>) filterResults.values;
                setSections(sectionList);
            } else {
                setSections(null);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ShowsFilter();
        return filter;
    }

    public void addSection(String caption, ArrayAdapter adapter) {
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
        if (sections != null) {
            for (Section section : this.sections) {
                total += section.adapter.getCount() + 1; // add one for header
            }
        }
        return total;
    }

    public int getViewTypeCount() {
        int total = 1;    // one for the header, plus those from sections
        if (sections != null) {
            for (Section section : this.sections) {
                total += section.adapter.getViewTypeCount();
            }
        }
        return total;
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

    public static class Section {
        public String caption;
        public ArrayAdapter adapter;

        public Section(String caption, ArrayAdapter adapter) {
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

    public Section getSection(String caption) {
        for (Section s : sections) {
            if (s.caption.equals(caption)) return s;
        }
        return null;
    }

    public void removeSection(String caption) {
        for (Section s : sections) {
            if (s.caption.equals(caption)) sections.remove(s);
        }
    }


}