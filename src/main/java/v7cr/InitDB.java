/**
 * Copyright (c) 2011, Thilo Planz. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package v7cr;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.IOUtils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

/**
 * Manages the connection to MongoDB
 */

public class InitDB implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent e) {

	}

	@SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent e) {
		try {
			Mongo db = new Mongo();

			ServletContext c = e.getServletContext();
			c.setAttribute(getClass().getName(), db);

			// check if the "roles" collection
			// exists, if not create it

			if (getDBCollection(c, "roles").findOne() == null) {
				String json = IOUtils.toString(getClass().getResourceAsStream(
						"roles.json"), "UTF-8");
				List<DBObject> l = (List<DBObject>) JSON.parse(json);
				DBCollection roles = getDBCollection(c, "roles");
				for (DBObject r : l) {
					r.put("_version", 1);
					roles.save(r);
				}

			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	static Mongo getMongo(ServletContext c) {
		return (Mongo) c.getAttribute(InitDB.class.getName());
	}

	static DB getDB(ServletContext c) {
		return getMongo(c).getDB("v7cr");
	}

	static DBCollection getDBCollection(ServletContext c, String name) {
		return getDB(c).getCollection(name);
	}

}
