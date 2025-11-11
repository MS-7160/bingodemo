import re

# Read the layout file
with open(r'C:\Users\Manson\AndroidStudioProjects\bingodemo\app\src\main\res\layout\activity_main.xml', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace textSize="18sp" with textSize="@dimen/bingo_cell_text_size" for cells (not headers)
# This targets the regular cells
content = re.sub(
    r'(android:id="@\+id/cell_\d+_\d+"[\s\S]*?)android:textSize="18sp"',
    r'\1android:textSize="@dimen/bingo_cell_text_size"',
    content
)

# Replace layout_margin="2dp" with layout_margin="@dimen/cell_margin" for all cells
content = re.sub(
    r'android:layout_margin="2dp"',
    r'android:layout_margin="@dimen/cell_margin"',
    content
)

# Update the FREE cell text size
content = re.sub(
    r'(android:id="@\+id/cell_2_2"[\s\S]*?)android:textSize="16sp"',
    r'\1android:textSize="@dimen/bingo_free_text_size"',
    content
)

# Update button dimensions
content = re.sub(
    r'(android:id="@\+id/actionButton"[\s\S]*?)android:textSize="16sp"',
    r'\1android:textSize="@dimen/button_text_size"',
    content
)

content = re.sub(
    r'android:layout_marginStart="32dp"\s+android:layout_marginEnd="32dp"\s+android:layout_marginBottom="24dp"',
    r'android:layout_marginStart="@dimen/button_margin_horizontal"\n        android:layout_marginEnd="@dimen/button_margin_horizontal"\n        android:layout_marginBottom="@dimen/button_margin_bottom"',
    content
)

# Write back
with open(r'C:\Users\Manson\AndroidStudioProjects\bingodemo\app\src\main\res\layout\activity_main.xml', 'w', encoding='utf-8') as f:
    f.write(content)

print("Layout updated successfully!")
