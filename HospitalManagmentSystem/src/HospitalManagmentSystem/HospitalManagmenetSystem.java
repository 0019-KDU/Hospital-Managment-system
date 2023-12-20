package HospitalManagmentSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagmenetSystem {
    private static final String url="jdbc:mysql://root:@localhost:3306/hospital";
    private static final String username="root";
    private static final String password="";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner=new Scanner(System.in);

        try{
            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient=new Patient(connection,scanner);
            Doctor doctor=new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGMENT SYSTEM");
                System.out.println("1.ADD PATIENTS");
                System.out.println("2.VIEW PATIENTS");
                System.out.println("3.VIEW DOCTOR");
                System.out.println("4.BOOK APPOINTMENT");
                System.out.println("5.EXIT");
                System.out.println("ENTER YOUR CHOICE:");

                int choice=scanner.nextInt();

                switch (choice){
                    case 1:
                        //add patients
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        //view patients
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        //view doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //Book Appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        break;
                    case 5:
                        System.out.println("THANK YOU FOR USEING OUR HOSPITAL MANAGMENT SYSTEM");
                        return;
                    default:
                        System.out.println("Enter Valid Number");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.println("Enter Patient Id:");
        int patientId=scanner.nextInt();
        System.out.println("Enter Doctor Id:");
        int doctorId=scanner.nextInt();
        System.out.println("Enter Appointment Date(YYYY-MM-DD):");
        String appointmentdate=scanner.next();

        if (patient.getPatientsById(patientId)&&doctor.getDoctorById(doctorId)){
                if (checkDoctorAvailability(doctorId,appointmentdate,connection)){
                    String appointmentQuery="INSERT INTO appointments(patient_id,doctor_id,appointment_date)";
                    try{
                        PreparedStatement preparedStatement=connection.prepareStatement(appointmentQuery);
                        preparedStatement.setInt(1,patientId);
                        preparedStatement.setInt(2,doctorId);
                        preparedStatement.setString(3,appointmentdate);
                        int affectedRows=preparedStatement.executeUpdate();
                        if (affectedRows>0){
                            System.out.println("Appointment Booked");
                        }else {
                            System.out.println("Faild to Book Appointment");
                        }
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                }
        }else {
            System.out.println("Either doctor or patient doesn't exist");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
