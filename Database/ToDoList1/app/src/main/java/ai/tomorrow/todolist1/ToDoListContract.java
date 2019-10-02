package ai.tomorrow.todolist1;

import android.provider.BaseColumns;

public class ToDoListContract {

    private ToDoListContract() {}

    public static class ToDoListEntry implements BaseColumns {
        public static final String TABLE_NAME = "todos";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
