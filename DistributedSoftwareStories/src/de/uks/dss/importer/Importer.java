package de.uks.dss.importer;

import static de.uks.dss.model.PredefinedDocumentDataConstants.NAME;
import static de.uks.dss.model.PredefinedDocumentDataConstants.dateFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.Task;
import de.uniks.networkparser.ext.io.ExcelBuffer;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.excel.ExcelCell;
import de.uniks.networkparser.parser.excel.ExcelRow;
import de.uniks.networkparser.parser.excel.ExcelSheet;

public class Importer
{
	public static void importPersons(String path, Task rootTask, String userName) throws IOException
	{
		/*Person clientGroup = rootTask.getPersons().filterName(CLIENTS).first();
		
		if(clientGroup == null)
		{
			clientGroup = rootTask.createPersons().withName(CLIENTS);
		}*/
				
		File myFile = new File(path);
		FileInputStream fis = new FileInputStream(myFile);

		ExcelBuffer exBuf = new ExcelBuffer();
		ExcelSheet sheet = exBuf.parse(myFile);

		//Tags: Teamname	Titel	Name	Vorname	Tel	E-Mail	Straße	PLZ	Ort
		//Status	Alumni seit	Fachbereich	Geschlecht	Teamgeschlecht	Teamgröße
		//Patentrelevanz	Kontakt wegen Patent	Eingangsdatum	Aufmerksam über
		ExcelRow tagRow = sheet.get(0);
		if(tagRow == null){
			return;
		}
		SimpleList<String> tags = new SimpleList<String>();
		for(ExcelCell tmpCell : tagRow)
		{
			tags.add(tmpCell.getContentAsString());
		}
		
		int teamNamePos = 0;
		int namePos = tags.indexOf("Name");
		int surnamePos = tags.indexOf("Vorname");
		
		// traversing over each row of XLSX file
		for (int rowNum = 1; rowNum < sheet.size(); rowNum++) {
			ExcelRow tmpRow = sheet.get(rowNum);

			String teamName = tmpRow.getItem(teamNamePos).getContentAsString();
			String memberName = tmpRow.getItem(namePos).getContentAsString();
			String memberSurname = tmpRow.getItem(surnamePos).getContentAsString();

			Person team = null;
			Person member = null;
			
			//traversing over each cell of current row
			for (int cellNum = 0; cellNum < tags.size(); cellNum++)
			{
				if(cellNum > 0)
				{
					String tag = tagRow.getItem(cellNum).getContentAsString();
					String tmpValue = tmpRow.getItem(cellNum).getContentAsString();
					
					//add person data if not existing for this tag and value
					if(member != null && !member.getPersonData().isEmpty()
							&& member.getPersonData().filterTag(tag).isEmpty()
							&& member.getPersonData().filterValue(tmpValue).isEmpty())
					{
						member.createPersonData()
							.withTag(tag)
							.withValue(tmpValue)
							.withType(DocumentDataType.STRING.toString()) // TODO - Check if it's a Date
							.withLastEditor(userName)
							.withLastModified(dateFormat.format(new Date(System.currentTimeMillis())));
					}
					else
					{
						member = rootTask.getPersons().filterName(teamName).getMembers().filter(new Condition<Person>() {
							@Override
							public boolean update(Person value) {
								if(!value.getPersonData().filterValue(memberName).isEmpty() && 
										!value.getPersonData().filterValue(memberSurname).isEmpty())
								{
									return true;
								}
								else
								{
									return false;
								}
							}
						}).first();

						if(member == null)
						{
							member = team.createMembers();
							member.withName(memberName);
							member.createPersonData()
								.withTag(NAME)
								.withValue(memberName)
								.withType(DocumentDataType.STRING.toString())
								.withLastEditor(userName)
								.withLastModified(dateFormat.format(new Date(System.currentTimeMillis())));
							member.createPersonData()
								.withTag("Vorname")
								.withValue(memberSurname)
								.withType(DocumentDataType.STRING.toString())
								.withLastEditor(userName)
								.withLastModified(dateFormat.format(new Date(System.currentTimeMillis())));
						}
					}
				}
				else
				{
					if(rootTask.getPersons().isEmpty() || rootTask.getPersons().getPersonData().filterValue(teamName).isEmpty())
					{
						team = rootTask.createPersons();
						team.withName(teamName);
						team.createPersonData()
							.withTag(NAME)
							.withValue(teamName)
							.withType(DocumentDataType.STRING.toString())
							.withLastEditor(userName)
							.withLastModified(dateFormat.format(new Date(System.currentTimeMillis())));
					}
					else
					{
						team = rootTask.getPersons().filterPersonData(rootTask.getPersons().getPersonData().filterValue(teamName).first()).first();
						team.withName(teamName);
					}
				}
			}
		}
	}
}
