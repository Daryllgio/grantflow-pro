import React, { useEffect, useMemo, useState } from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from "recharts";
import {
  LayoutDashboard,
  FileText,
  Bell,
  ShieldCheck,
  LogOut,
  Briefcase,
  ClipboardCheck
} from "lucide-react";
import "./styles.css";

const API = "http://localhost:8080/api";

type Role = "APPLICANT" | "REVIEWER" | "PROGRAM_MANAGER" | "ADMIN";

type User = {
  userId: number;
  fullName: string;
  email: string;
  role: Role;
  token: string;
};

type Program = {
  id: number;
  name: string;
  description: string;
  awardAmount: number;
  applicationDeadline: string;
  eligibilityCriteria: string;
  status: string;
};

type Application = {
  id: number;
  program: Program;
  applicant: {
    fullName: string;
    email: string;
  };
  personalStatement: string;
  status: string;
  submittedAt: string | null;
};

function authHeaders() {
  const raw = localStorage.getItem("grantflow_user");
  if (!raw) return {};
  const user = JSON.parse(raw) as User;
  return { Authorization: `Bearer ${user.token}` };
}

function App() {
  const [user, setUser] = useState<User | null>(() => {
    const raw = localStorage.getItem("grantflow_user");
    return raw ? JSON.parse(raw) : null;
  });

  if (!user) return <AuthScreen onAuth={setUser} />;

  return (
    <Dashboard
      user={user}
      onLogout={() => {
        localStorage.removeItem("grantflow_user");
        setUser(null);
      }}
    />
  );
}

function AuthScreen({ onAuth }: { onAuth: (user: User) => void }) {
  const [mode, setMode] = useState<"login" | "register">("login");
  const [fullName, setFullName] = useState("Demo User");
  const [email, setEmail] = useState("admin@grantflow.dev");
  const [password, setPassword] = useState("password123");
  const [role, setRole] = useState<Role>("APPLICANT");
  const [error, setError] = useState("");

  async function submit() {
    try {
      setError("");
      const url = mode === "login" ? `${API}/auth/login` : `${API}/auth/register`;
      const body = mode === "login" ? { email, password } : { fullName, email, password, role };
      const res = await axios.post(url, body);
      localStorage.setItem("grantflow_user", JSON.stringify(res.data));
      onAuth(res.data);
    } catch (e: any) {
      setError(e.response?.data?.error || "Authentication failed");
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="badge">GrantFlow Pro</div>
        <h1>Nonprofit grant operations, built like real enterprise software.</h1>
        <p>
          Manage programs, applicants, scoring, reviewer workflows, audit logs,
          notifications, and scholarship decisions.
        </p>

        <div className="form">
          {mode === "register" && (
            <>
              <input value={fullName} onChange={e => setFullName(e.target.value)} placeholder="Full name" />
              <select value={role} onChange={e => setRole(e.target.value as Role)}>
                <option value="APPLICANT">Applicant</option>
                <option value="REVIEWER">Reviewer</option>
                <option value="PROGRAM_MANAGER">Program Manager</option>
                <option value="ADMIN">Admin</option>
              </select>
            </>
          )}

          <input value={email} onChange={e => setEmail(e.target.value)} placeholder="Email" />
          <input value={password} onChange={e => setPassword(e.target.value)} placeholder="Password" type="password" />

          {error && <div className="error">{error}</div>}

          <button onClick={submit}>{mode === "login" ? "Login" : "Create account"}</button>
          <button className="ghost" onClick={() => setMode(mode === "login" ? "register" : "login")}>
            Switch to {mode === "login" ? "register" : "login"}
          </button>
        </div>

        <div className="demo-box">
          <strong>Demo logins</strong>
          <span>admin@grantflow.dev / password123</span>
          <span>reviewer@grantflow.dev / password123</span>
          <span>applicant@grantflow.dev / password123</span>
        </div>
      </div>
    </div>
  );
}

function Dashboard({ user, onLogout }: { user: User; onLogout: () => void }) {
  const [programs, setPrograms] = useState<Program[]>([]);
  const [applications, setApplications] = useState<Application[]>([]);
  const [stats, setStats] = useState<any>({});
  const [view, setView] = useState("dashboard");

  useEffect(() => {
    load();
  }, [view]);

  async function load() {
    try {
      const programEndpoint =
        user.role === "APPLICANT" ? `${API}/programs/open` : `${API}/programs`;

      const appEndpoint =
        user.role === "APPLICANT" ? `${API}/applications/my` : `${API}/applications`;

      const p = await axios.get(programEndpoint, { headers: authHeaders() });
      setPrograms(p.data);

      if (user.role !== "REVIEWER") {
        const a = await axios.get(appEndpoint, { headers: authHeaders() });
        setApplications(a.data);
      }

      if (user.role === "ADMIN" || user.role === "PROGRAM_MANAGER") {
        const s = await axios.get(`${API}/dashboard/admin`, { headers: authHeaders() });
        setStats(s.data);
      }
    } catch (e) {
      console.log(e);
    }
  }

  const statusData = useMemo(() => {
    return Object.entries(stats)
      .filter(([key]) => ["submitted", "under_review", "approved", "rejected", "waitlisted"].includes(key))
      .map(([name, value]) => ({ name, value }));
  }, [stats]);

  return (
    <div className="app-shell">
      <aside>
        <div className="brand">GrantFlow Pro</div>
        <nav>
          <button onClick={() => setView("dashboard")}><LayoutDashboard size={18} /> Dashboard</button>
          <button onClick={() => setView("programs")}><Briefcase size={18} /> Programs</button>
          <button onClick={() => setView("applications")}><FileText size={18} /> Applications</button>
          <button onClick={() => setView("reviewer")}><ClipboardCheck size={18} /> Reviewer Queue</button>
          <button onClick={() => setView("notifications")}><Bell size={18} /> Notifications</button>
          <button onClick={() => setView("audit")}><ShieldCheck size={18} /> Audit Logs</button>
        </nav>
        <button className="logout" onClick={onLogout}><LogOut size={18} /> Logout</button>
      </aside>

      <main>
        <header>
          <div>
            <h2>{viewTitle(view)}</h2>
            <p>{user.fullName} · {user.role}</p>
          </div>
          <span className="role-pill">{user.role}</span>
        </header>

        {view === "dashboard" && (
          <>
            <div className="cards">
              <Stat title="Programs" value={stats.programs ?? programs.length} />
              <Stat title="Applications" value={stats.applications ?? applications.length} />
              <Stat title="Reviews" value={stats.reviews ?? 0} />
              <Stat title="Approved" value={stats.approved ?? 0} />
            </div>

            <section className="grid">
              <div className="panel">
                <h3>Application Status</h3>
                <ResponsiveContainer width="100%" height={260}>
                  <BarChart data={statusData}>
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="value" />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="panel">
                <h3>Status Distribution</h3>
                <ResponsiveContainer width="100%" height={260}>
                  <PieChart>
                    <Pie data={statusData} dataKey="value" nameKey="name" outerRadius={90}>
                      {statusData.map((_, index) => <Cell key={index} />)}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </section>
          </>
        )}

        {view === "programs" && <Programs programs={programs} onCreated={load} user={user} />}
        {view === "applications" && <Applications applications={applications} programs={programs} onCreated={load} user={user} />}
        {view === "reviewer" && <ReviewerQueue />}
        {view === "notifications" && <Notifications />}
        {view === "audit" && <AuditLogs />}
      </main>
    </div>
  );
}

function viewTitle(view: string) {
  return {
    dashboard: "Operations Dashboard",
    programs: "Funding Programs",
    applications: "Applications",
    reviewer: "Reviewer Queue",
    notifications: "Notifications",
    audit: "Audit Logs"
  }[view] || "Dashboard";
}

function Stat({ title, value }: { title: string; value: any }) {
  return (
    <div className="stat">
      <span>{title}</span>
      <strong>{value}</strong>
    </div>
  );
}

function Programs({ programs, onCreated, user }: { programs: Program[]; onCreated: () => void; user: User }) {
  const [name, setName] = useState("New Impact Scholarship");
  const [description, setDescription] = useState("Supports students with leadership potential and financial need.");

  async function createProgram() {
    await axios.post(`${API}/programs`, {
      name,
      description,
      awardAmount: 1000,
      applicationDeadline: "2026-08-31",
      eligibilityCriteria: "Applicants must demonstrate leadership, academic promise, and community impact."
    }, { headers: authHeaders() });

    onCreated();
  }

  return (
    <div>
      {(user.role === "ADMIN" || user.role === "PROGRAM_MANAGER") && (
        <div className="panel form-inline">
          <h3>Create Program</h3>
          <input value={name} onChange={e => setName(e.target.value)} />
          <input value={description} onChange={e => setDescription(e.target.value)} />
          <button onClick={createProgram}>Create Program</button>
        </div>
      )}

      <div className="table-card">
        {programs.map(program => (
          <div className="row" key={program.id}>
            <div>
              <strong>{program.name}</strong>
              <p>{program.description}</p>
            </div>
            <span className="status">{program.status}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function Applications({ applications, programs, onCreated, user }: {
  applications: Application[];
  programs: Program[];
  onCreated: () => void;
  user: User;
}) {
  const [programId, setProgramId] = useState<number | "">("");
  const [statement, setStatement] = useState("I am applying because this opportunity aligns with my academic, leadership, and community goals.");

  async function createApplication() {
    if (!programId) return;

    const res = await axios.post(`${API}/applications`, {
      programId,
      personalStatement: statement,
      academicBackground: "Strong academic record and demonstrated interest in community work.",
      financialNeedStatement: "Funding would reduce financial barriers and support continued education.",
      leadershipExperience: "Led student and community initiatives.",
      communityImpact: "Committed to creating measurable impact through service."
    }, { headers: authHeaders() });

    await axios.post(`${API}/applications/${res.data.id}/submit`, {}, { headers: authHeaders() });
    onCreated();
  }

  async function moveStatus(id: number, status: string) {
    await axios.patch(`${API}/applications/${id}/status`, { status }, { headers: authHeaders() });
    onCreated();
  }

  return (
    <div>
      {user.role === "APPLICANT" && (
        <div className="panel form-inline">
          <h3>Start Application</h3>
          <select value={programId} onChange={e => setProgramId(Number(e.target.value))}>
            <option value="">Select program</option>
            {programs.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>
          <textarea value={statement} onChange={e => setStatement(e.target.value)} />
          <button onClick={createApplication}>Submit Application</button>
        </div>
      )}

      <div className="table-card">
        {applications.map(app => (
          <div className="row" key={app.id}>
            <div>
              <strong>{app.program?.name}</strong>
              <p>{app.applicant?.fullName} · {app.personalStatement?.slice(0, 120)}...</p>
            </div>
            <div className="row-actions">
              <span className="status">{app.status}</span>
              {(user.role === "ADMIN" || user.role === "PROGRAM_MANAGER") && (
                <>
                  <button onClick={() => moveStatus(app.id, "UNDER_REVIEW")}>Review</button>
                  <button onClick={() => moveStatus(app.id, "APPROVED")}>Approve</button>
                  <button onClick={() => moveStatus(app.id, "REJECTED")}>Reject</button>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

function ReviewerQueue() {
  const [assignments, setAssignments] = useState<any[]>([]);

  useEffect(() => {
    axios.get(`${API}/reviewer/assignments`, { headers: authHeaders() })
      .then(res => setAssignments(res.data))
      .catch(() => setAssignments([]));
  }, []);

  async function submitReview(applicationId: number) {
    await axios.post(`${API}/applications/${applicationId}/reviews`, {
      academicScore: 18,
      leadershipScore: 19,
      financialNeedScore: 17,
      communityImpactScore: 20,
      essayScore: 18,
      recommendation: "APPROVE",
      feedback: "Strong applicant with clear leadership experience and meaningful community impact."
    }, { headers: authHeaders() });

    window.location.reload();
  }

  return (
    <div className="table-card">
      {assignments.length === 0 && <p className="muted">No reviewer assignments yet.</p>}
      {assignments.map(a => (
        <div className="row" key={a.id}>
          <div>
            <strong>{a.application?.program?.name}</strong>
            <p>Applicant: {a.application?.applicant?.fullName}</p>
          </div>
          <button onClick={() => submitReview(a.application.id)}>Submit Review</button>
        </div>
      ))}
    </div>
  );
}

function Notifications() {
  const [items, setItems] = useState<any[]>([]);

  useEffect(() => {
    axios.get(`${API}/notifications`, { headers: authHeaders() })
      .then(res => setItems(res.data));
  }, []);

  return (
    <div className="table-card">
      {items.map(n => (
        <div className="row" key={n.id}>
          <div>
            <strong>{n.title}</strong>
            <p>{n.message}</p>
          </div>
          <span className="status">{n.read ? "READ" : "NEW"}</span>
        </div>
      ))}
    </div>
  );
}

function AuditLogs() {
  const [items, setItems] = useState<any[]>([]);

  useEffect(() => {
    axios.get(`${API}/audit-logs`, { headers: authHeaders() })
      .then(res => setItems(res.data))
      .catch(() => setItems([]));
  }, []);

  return (
    <div className="table-card">
      {items.length === 0 && <p className="muted">Audit logs are only visible to admins.</p>}
      {items.map(log => (
        <div className="row" key={log.id}>
          <div>
            <strong>{log.action}</strong>
            <p>{log.details}</p>
          </div>
          <span className="status">{log.entityType}</span>
        </div>
      ))}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")!).render(<App />);
