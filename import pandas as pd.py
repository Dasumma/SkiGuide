import pandas as pd
import folium
import re

# 1. Load the data
# Assuming your file is named 'ski_data.csv'
# If you are copy-pasting the text above, replace 'ski_data.csv' with your filename
df = pd.read_csv('C:\\Users\\Dasum\\downloads\\neo4j_query_table_data_2026-2-19.csv')

def extract_coords(node_string):
    """Extracts lat and lon from the (:Point {lat: ..., lon: ...}) string."""
    lat = re.search(r'lat:\s*([-+]?\d*\.\d+|\d+)', node_string)
    lon = re.search(r'lon:\s*([-+]?\d*\.\d+|\d+)', node_string)
    if lat and lon:
        return float(lat.group(1)), float(lon.group(1))
    return None, None

# 2. Create the Map
# Centering the map around the first point in the dataset
first_lat, first_lon = extract_coords(df['p'].iloc[0])
m = folium.Map(location=[first_lat, first_lon], zoom_start=17, tiles='OpenStreetMap')

# 3. Process each row to draw the lines (SEGMENTS)
for index, row in df.iterrows():
    p_lat, p_lon = extract_coords(row['p'])
    q_lat, q_lon = extract_coords(row['q'])
    
    if p_lat and q_lat:
        # Extract metadata for the tooltip (Slope and Distance)
        slope = re.search(r'slope:\s*([-+]?\d*\.\d+|\d+)', row['r']).group(1)
        dist = re.search(r'distance:\s*([-+]?\d*\.\d+|\d+)', row['r']).group(1)
        
        # Color line based on slope (Example: Red if steep > 15%)
        color = 'red' if float(slope) > 15 else 'blue'
        
        folium.PolyLine(
            locations=[(p_lat, p_lon), (q_lat, q_lon)],
            color=color,
            weight=5,
            opacity=0.8,
            tooltip=f"Slope: {float(slope):.2f}% | Dist: {float(dist):.2f}m"
        ).add_to(m)

# 4. Save and view
m.save('C:\\Users\\Dasum\\ski_map.html')
print("Map has been created as 'ski_map.html'. Open this file in your browser.")