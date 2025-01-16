from SPARQLWrapper import SPARQLWrapper, JSON, XML
import os
import glob
import xml.etree.ElementTree as ET
import xmltodict
import json


# Provide the directory path
directory_path_1 = 'moving_db/'
directory_path_2 = 'moving_db_2/'


# Set up the SPARQL endpoint URL
# sparql = SPARQLWrapper("http://localhost:3030/gsbenchmark/query")
# sparql = SPARQLWrapper("https://testable.isti.cnr.it/fuseki/seminar-openllet/query")
# sparql = SPARQLWrapper("https://testable.isti.cnr.it/fuseki/seminar-openllet/update")


def read_files_in_directory(directory):
    file_contents = {}
    
    for filename in os.listdir(directory):
        file_path = os.path.join(directory, filename)
        
        if os.path.isfile(file_path):
            with open(file_path, 'r') as file:
                file_contents[filename] = file.read()
    
    return file_contents

def read_answer(directory, filename):
    # Find files matching the partial filename
    matching_files = glob.glob(directory + '*' + filename + '*')

    # Read the content of the first matching file
    if matching_files:
        file_path = matching_files[0]
        with open(file_path, 'r') as file:
            file_content = file.read()
            # print(file_content)
            return file_content
    else:
        print("No matching files found.")

def check_query_result(result, answer):
    # Parse the XML responses
    root_1 = ET.fromstring(result)
    root_2 = ET.fromstring(answer)
    result_1 = xmltodict.parse(result)
    result_2 = xmltodict.parse(answer)
    xka=result_1["sparql"]["results"]["result"]
    xkb=result_2["sparql"]["results"]["result"]
    # print(f"Result1: {xka}")
    # print(f"Result2: {xkb}")
    xxxx = []
    for xka_x in xka:
        xxxx.append(xka_x)
        
    yyyy = []
    for xkb_x in xkb:
        yyyy.append(xkb_x)

    # Compare the results
    if xxxx == yyyy:
        print("TRUE")
        return True
    else:
        print("FALSE")
        # print(f"result_1: {result_1}")
        # print(f"XXXX: {xxxx}")
        # print(f"YYYY: {yyyy}")
        return False
    
# Set the SPARQL query string
# query = """

# PREFIX gml: <http://www.opengis.net/ont/gml#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# SELECT DISTINCT ?f
# WHERE {
#   ?f rdf:type gml:Surface
# }
# ORDER BY ?f

# """

# Print the results
# for result in results["results"]["bindings"]:
#     print(result["equals"]["value"])






# Call the function to read files in the directory
contents = read_files_in_directory(directory_path_1)
contents_2 = read_files_in_directory(directory_path_2)


# Display the file contents
for filename, content in contents_2.items():
    # Remove the extension from the filename
    filename_without_extension = os.path.splitext(filename)[0]
    print(f"Filename: {filename_without_extension}")
    # print(f"Content: {content}")
    j = json.loads(content)
    # print(f"Content: {j['narra']}")
    # Search data based on key and value using filter and list method
    for k in j['events']:        
        if(j['events'][k]['title']=="Geography and population"):
            # print(k, j['events'][k]['polygon'])
            j['narra']['place'] =  j['events'][k]['polygon']
    
    jsonString = json.dumps(j)
    jsonFile = open("mdb2/"+filename, "w")
    jsonFile.write(jsonString)
    jsonFile.close()
    # queryString = "DELETE WHERE { ?s ?p ?o. }" 
    # Set the query type to SELECT and the response format to JSON
    