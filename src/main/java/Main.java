import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        // csv to json
        List<Employee> list = parceCSV(columnMapping, fileName);
        String jsonFromCSV = listToJson(list);
        writeString(jsonFromCSV, "json_content.json");
        // xml to json
        List<Employee> listXml = parseXML("data.xml");
        String jsonFromXML = listToJson(listXml);
        writeString(jsonFromXML, "data2.json");
        // json to object
        String json = readString("data2.json");
        List<Employee> employees = jsonToList(json);
        for (int i = 0; i < employees.size(); i++) {
            System.out.println(employees.get(i));
        }
    }

    public static List<Employee> parceCSV(String[] columnMapping, String fileName) {
        List<Employee> employees = null;
        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            employees = csv.parse();

    } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return employees;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }
    public static void writeString(String json, String fileToWrite) {
        try(FileWriter writer = new FileWriter(fileToWrite)) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    // метод для работы с xml
    public static List<Employee> parseXML(String fileName) {
        List<Employee> listToReturn = new ArrayList<>();
        try{
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(fileName);
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node employee = nodeList.item(i);
                if (Node.ELEMENT_NODE == employee.getNodeType()) {
                    //System.out.println("Узел: " + employee.getNodeName());
                    Element element = (Element) employee;
                    NodeList childNodesOfEmployees = element.getChildNodes();
                    int id = 0;
                    String firstName = null;
                    String lastName = null;
                    String country = null;
                    int age = 0;
                    for (int j = 0; j < childNodesOfEmployees.getLength(); j++) {
                        Node someAtribute = childNodesOfEmployees.item(j);
                        if (Node.ELEMENT_NODE == someAtribute.getNodeType()) {
                            // получение параметров для создания сотрудника employee
                            //System.out.println("Текущий узел: " + someAtribute.getNodeName());
                            //System.out.println("Содержимое: " + someAtribute.getTextContent());
                            String content = someAtribute.getTextContent();
                            switch (someAtribute.getNodeName()) {
                                case "id":
                                    id = Integer.parseInt(content);
                                    break;
                                case "firstName":
                                    firstName = content;
                                    break;
                                case "lastName":
                                    lastName = content;
                                    break;
                                case "country":
                                    country = content;
                                    break;
                                case "age":
                                    age = Integer.parseInt(content);
                                    break;
                            }
                        }
                    }
                    Employee someEmployee = new Employee(id, firstName, lastName, country, age);
                    // Можно проследить, что сотрудник создан
                    //System.out.println("Сотрудник " + firstName + " " + lastName + " создан.");
                    listToReturn.add(someEmployee);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return listToReturn;
    }
    // метод для прочтения json-файла и возврата строки
    public static String readString(String fileName) {
        String json = "";
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            json = reader.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return json;
    }
    // метод для преобразования прочитанного json-файла в список объектов
    public static List<Employee> jsonToList(String json) {
        List<Employee> arrayOfEmployees = new ArrayList<>();
        JSONParser parser = new JSONParser();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            for (Object jsonObj : jsonArray) {
                Employee someEmployee = gson.fromJson(jsonObj.toString(), Employee.class);
                arrayOfEmployees.add(someEmployee);
                // Можно проследить, что сотрудник создан
                //System.out.println("Сотрудник " + someEmployee.firstName
                // + " " + someEmployee.lastName + " создан.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return arrayOfEmployees;
    }
}
