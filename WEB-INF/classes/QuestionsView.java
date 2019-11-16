import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class QuestionsView extends HttpServlet{
	public Connection dbSetup() throws SQLException,ClassNotFoundException{
		String dbDriver = "com.mysql.jdbc.Driver";
		String host = "jdbc:mysql://localhost:3306/";
		String dbName = "mcq";
		String dbUser = "root";
		String dbPassword = "";

		Class.forName(dbDriver);
		Connection con = DriverManager.getConnection(host+dbName,dbUser,dbPassword);

		return con;
	}

	public ResultSet retrieveQuestions() throws SQLException,ClassNotFoundException{
		Connection con = dbSetup();
		PreparedStatement st = con.prepareStatement("SELECT * FROM questions");
		ResultSet rs = st.executeQuery();
		// con.close();
		return rs;
	}

	public ResultSet retrieveAnswer(int qid) throws SQLException,ClassNotFoundException{
		Connection con = dbSetup();
		PreparedStatement st = con.prepareStatement("SELECT * FROM answers WHERE qid=?");
		st.setInt(1,qid);
		ResultSet rs = st.executeQuery();
		// con.close();

		return rs;
	}

	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		try{
			ResultSet questions = retrieveQuestions();
			writer.println("<h1>WP QUIZ</h1><form method='POST'>");
			while(questions.next()){
				String question = questions.getString("question");
				writer.println("<h5>" + question + "</h5>");
				ResultSet answers = retrieveAnswer(questions.getInt("qid"));
				// writer.println("<select name=" + questions.getInt("qid") + ">");
				while(answers.next()){
					writer.println("<input type='radio' name=" + questions.getInt("qid") +" value="+answers.getInt("ansid")+">"+ answers.getString("answer") +"</option>");
				}
				// writer.println("</select>");
			}
			writer.println("<input type='submit' value='Submit'></form>");


		}
		catch(Exception e){
			writer.println(e);
		}
	}

	public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		try{
			int ques_count = 0;
			int score = 0;
			ResultSet questions = retrieveQuestions();
			while(questions.next()){
				ques_count++;
				int given_ans = Integer.parseInt(request.getParameter(Integer.toString(questions.getInt("qid"))));
				// ResultSet answers = retrieveAnswer();
				Connection con = dbSetup();
				// PreparedStatement st = con.prepareStatement("SELECT question FROM questions WHERE id=?");
				// st.setInt(1,given_ans);
				// ResultSet rs = st.executeQuery();
				// while(rs.next()){
				writer.println("<h5>Question : " + questions.getString("question")+"</h5>");
				// }
				PreparedStatement st = con.prepareStatement("SELECT * FROM answers WHERE qid=? AND is_correct=1");
				st.setInt(1,questions.getInt("qid"));
				ResultSet rs = st.executeQuery();
				// writer.println(st);
				int flag = 0;
				int count_correct_ans = 0;
				String corr_ans[] = new String[4];
				while(rs.next()){
					int correct_ans = rs.getInt("ansid");
					corr_ans[count_correct_ans] = rs.getString("answer");
					count_correct_ans++;
					if(given_ans == correct_ans){
						flag = 1;
					}
				}
				if(flag == 1){
					writer.println("Correct <br>");
					score++;
				}else{
					writer.println("<b>Wrong</b><br>Correct Answer : <br>");
					for(int i =0;i<count_correct_ans;i++){
						writer.println(corr_ans[i] + "<br>");
					}
				}
				// int correct_ans = 0;
				// while(answers.next())
			}

			writer.println("<h2> Score : " + score + "/" + ques_count +"</h2>");
			// writer.println("<h1>WP QUIZ</h1><form method='POST'>");
			


		}
		catch(Exception e){
			// writer.println(e);
			e.printStackTrace(writer);
		}
	}
}