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
  Eye,
  UploadCloud,
  CheckCircle2,
  Save
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
  academicBackground: string;
  financialNeedStatement: string;
  leadershipExperience: string;
  communityImpact: string;
  status: string;
  submittedAt: string | null;
};

type ApplicationDocument = {
  id: number;
  documentType: string;
  fileName: string;
  storageKey: string;
  documentUrl: string;
  uploadedAt: string;
};

type Review = {
  id: number;
  academicScore: number;
  leadershipScore: number;
  financialNeedScore: number;
  communityImpactScore: number;
  essayScore: number;
  totalScore: number;
  recommendation: string;
  feedback: string;
  reviewedAt: string;
  reviewer: {
    fullName: string;
    email: string;
  };
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
  const [email, setEmail] = useState("admin@grantflow.dev");
  const [password, setPassword] = useState("password123");
  const [error, setError] = useState("");

  async function submit() {
    try {
      setError("");
      const res = await axios.post(`${API}/auth/login`, { email, password });
      localStorage.setItem("grantflow_user", JSON.stringify(res.data));
      onAuth(res.data);
    } catch (e: any) {
      setError(e.response?.data?.error || "Authentication failed");
    }
  }

  function fillDemo(selectedEmail: string) {
    setEmail(selectedEmail);
    setPassword("password123");
  }

  return (
    <div className="auth-page">
      <div className="auth-card improved-auth centered-auth">
        <div className="badge">GrantFlow Pro</div>
        <h1>Grant and scholarship<br />management system</h1>
        <p>
          A secure platform for managing funding programs, applications, documents,
          reviews, decisions, and applicant updates.
        </p>

        <div className="demo-instruction">
          Select a demo role below to auto-fill the login credentials.
        </div>

        <div className="demo-grid">
          <button className="demo-card" onClick={() => fillDemo("admin@grantflow.dev")}>
            <strong>Admin Dashboard</strong>
            <span>admin@grantflow.dev</span>
            <small>Password: password123</small>
          </button>

          <button className="demo-card" onClick={() => fillDemo("applicant@grantflow.dev")}>
            <strong>Applicant Portal</strong>
            <span>applicant@grantflow.dev</span>
            <small>Password: password123</small>
          </button>
        </div>

        <div className="form">
          <input value={email} onChange={e => setEmail(e.target.value)} placeholder="Email address" />
          <input value={password} onChange={e => setPassword(e.target.value)} placeholder="Password" type="password" />

          {error && <div className="error">{error}</div>}

          <button onClick={submit}>Sign in</button>
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
    const source = applications.reduce((acc: any, app) => {
      const key = app.status.toLowerCase();
      acc[key] = (acc[key] || 0) + 1;
      return acc;
    }, {});

    return [
      { name: "Submitted", key: "submitted" },
      { name: "Under Review", key: "under_review" },
      { name: "Approved", key: "approved" },
      { name: "Rejected", key: "rejected" },
      { name: "Waitlisted", key: "waitlisted" }
    ]
      .map(item => ({ name: item.name, value: source[item.key] || 0 }))
      .filter(item => item.value > 0);
  }, [applications]);

  const chartColors = ["#2563eb", "#f59e0b", "#16a34a", "#dc2626", "#7c3aed"];

  return (
    <div className="app-shell">
      <aside>
        <div className="brand">GrantFlow Pro</div>
        <nav>
          <button onClick={() => setView("dashboard")}><LayoutDashboard size={18} /> Dashboard</button>
          <button onClick={() => setView("programs")}><Briefcase size={18} /> Programs</button>
          <button onClick={() => setView("applications")}><FileText size={18} /> Applications</button>
          {user.role === "APPLICANT" && <button onClick={() => setView("notifications")}><Bell size={18} /> Notifications</button>}
          {user.role === "ADMIN" && <button onClick={() => setView("audit")}><ShieldCheck size={18} /> Audit Logs</button>}
        </nav>
        <button className="logout" onClick={onLogout}><LogOut size={18} /> Logout</button>
      </aside>

      <main>
        <header>
          <div>
            <h2>{viewTitle(view)}</h2>
          </div>
          <span className="role-pill">{user.role}</span>
        </header>

        {view === "dashboard" && (
          <>
            <div className="cards">
              <Stat title="Programs" value={programs.length} />
              <Stat title="Applications" value={applications.length} />
              <Stat title="Under Review" value={applications.filter(a => a.status === "UNDER_REVIEW").length} />
              <Stat title="Approved" value={applications.filter(a => a.status === "APPROVED").length} />
            </div>

            <section className="grid">
              <div className="panel">
                <h3>Application Status</h3>
                <ResponsiveContainer width="100%" height={260}>
                  <BarChart data={statusData}>
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="value">
                      {statusData.map((_, index) => (
                        <Cell key={`bar-cell-${index}`} fill={chartColors[index % chartColors.length]} />
                      ))}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="panel">
                <h3>Status Distribution</h3>
                <ResponsiveContainer width="100%" height={260}>
                  <PieChart>
                    <Pie data={statusData} dataKey="value" nameKey="name" outerRadius={90}>
                      {statusData.map((_, index) => <Cell key={index} fill={chartColors[index % chartColors.length]} />)}
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
          <div className="prompt-block">
            <label>Program Name</label>
            <input value={name} onChange={e => setName(e.target.value)} />
          </div>

          <div className="prompt-block">
            <label>Program Description</label>
            <textarea value={description} onChange={e => setDescription(e.target.value)} />
          </div>

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

function getProgramPrefill(programName?: string) {
  const name = (programName || "").toLowerCase();

  if (name.includes("gwags")) {
    return {
      prompt1: "Describe your academic goals and why this scholarship matters at this stage of your education.",
      answer1: "I am pursuing this opportunity because it would directly support my academic progress and reduce the financial pressure that often limits student success. My goal is to continue building a strong academic foundation while using my education to serve my community.",
      prompt2: "Describe a leadership or service experience that shaped your commitment to impact.",
      answer2: "One leadership experience that shaped me was organizing and supporting student-led initiatives focused on access, mentorship, and community development. That experience taught me that leadership is not only about holding a title, but about creating structures that help others succeed.",
      prompt3: "How would receiving this award help you create long-term community impact?",
      answer3: "Receiving this award would allow me to focus more fully on my studies and service work. In the long term, I hope to use my education to expand opportunities for students facing similar financial and structural barriers."
    };
  }

  if (name.includes("leadership") || name.includes("microgrant")) {
    return {
      prompt1: "What community problem does your project address?",
      answer1: "My project addresses a clear gap in access to resources, mentorship, and practical support for young people trying to build sustainable community initiatives.",
      prompt2: "What actions will you take if selected?",
      answer2: "If selected, I will use the funding to organize outreach, coordinate volunteers, purchase essential materials, and track measurable outcomes so the project can be improved and repeated.",
      prompt3: "How will you measure success?",
      answer3: "I will measure success through participation numbers, direct beneficiary feedback, completion of planned activities, and evidence that the project created a useful and repeatable community benefit."
    };
  }

  return {
    prompt1: "Why are you applying for this funding opportunity?",
    answer1: "I am applying because this opportunity aligns with my academic, leadership, and community goals. It would provide meaningful support while allowing me to continue building skills that can benefit others.",
    prompt2: "What experiences make you a strong candidate?",
    answer2: "My academic background, leadership experience, and commitment to service make me a strong candidate. I have consistently sought opportunities to take responsibility, solve problems, and contribute to my community.",
    prompt3: "How will this funding support your next step?",
    answer3: "This funding would reduce financial barriers, help me focus on my goals, and allow me to continue pursuing opportunities that create long-term educational and community impact."
  };
}

function Applications({ applications, programs, onCreated, user }: {
  applications: Application[];
  programs: Program[];
  onCreated: () => void;
  user: User;
}) {
  const [programId, setProgramId] = useState<number | "">("");
  const selectedProgram = programs.find(p => p.id === programId);
  const prefill = getProgramPrefill(selectedProgram?.name);

  const [answer1, setAnswer1] = useState(prefill.answer1);
  const [answer2, setAnswer2] = useState(prefill.answer2);
  const [answer3, setAnswer3] = useState(prefill.answer3);

  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [documentType, setDocumentType] = useState("RESUME");
  const [message, setMessage] = useState("");
  const [selectedApplication, setSelectedApplication] = useState<Application | null>(null);
  const [selectedDocs, setSelectedDocs] = useState<ApplicationDocument[]>([]);
  const [selectedReviews, setSelectedReviews] = useState<Review[]>([]);
  const [editingDraftId, setEditingDraftId] = useState<number | null>(null);

  useEffect(() => {
    const current = getProgramPrefill(selectedProgram?.name);
    setAnswer1(current.answer1);
    setAnswer2(current.answer2);
    setAnswer3(current.answer3);
    setMessage("");
  }, [programId]);

  async function createApplication(status: "draft" | "submit") {
    if (!programId) {
      setMessage("Select a program first.");
      return;
    }

    const payload = {
      programId,
      personalStatement: answer1,
      academicBackground: answer2,
      financialNeedStatement: answer3,
      leadershipExperience: "",
      communityImpact: ""
    };

    let applicationId = editingDraftId;

    if (editingDraftId) {
      const updated = await axios.patch(`${API}/applications/${editingDraftId}`, payload, { headers: authHeaders() });
      applicationId = updated.data.id;
    } else {
      const created = await axios.post(`${API}/applications`, payload, { headers: authHeaders() });
      applicationId = created.data.id;
    }

    for (const file of selectedFiles) {
      const formData = new FormData();
      formData.append("documentType", documentType);
      formData.append("file", file);

      await axios.post(`${API}/applications/${applicationId}/documents/upload`, formData, {
        headers: {
          ...authHeaders(),
          "Content-Type": "multipart/form-data"
        }
      });
    }

    if (status === "submit") {
      await axios.post(`${API}/applications/${applicationId}/submit`, {}, { headers: authHeaders() });
      setMessage("Application submitted successfully.");
    } else {
      setMessage("Application saved as draft.");
    }

    setSelectedFiles([]);
    setEditingDraftId(null);
    setTimeout(() => setMessage(""), 2500);
    onCreated();
  }

  function editDraft(app: Application) {
    setProgramId(app.program.id);
    setAnswer1(app.personalStatement || "");
    setAnswer2(app.academicBackground || "");
    setAnswer3(app.financialNeedStatement || "");
    setEditingDraftId(app.id);
    setMessage("Draft loaded. Make changes, then save or submit.");
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  async function moveStatus(id: number, status: string) {
    await axios.patch(`${API}/applications/${id}/status`, { status }, { headers: authHeaders() });
    onCreated();
  }

  async function openApplication(app: Application) {
    setSelectedApplication(app);

    try {
      const docs = await axios.get(`${API}/applications/${app.id}/documents/list`, { headers: authHeaders() });
      setSelectedDocs(docs.data);
    } catch {
      setSelectedDocs([]);
    }

    try {
      const reviews = await axios.get(`${API}/applications/${app.id}/reviews`, { headers: authHeaders() });
      setSelectedReviews(reviews.data);
    } catch {
      setSelectedReviews([]);
    }
  }

  return (
    <div>
      {user.role === "APPLICANT" && (
        <div className="panel form-inline">
          <h3>Application Form</h3>
          <p className="helper">Select a program, complete the responses, attach documents, then save as draft or submit.</p>

          <select value={programId} onChange={e => setProgramId(Number(e.target.value))}>
            <option value="">Select program</option>
            {programs.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>

          {!selectedProgram && (
            <div className="empty-application-state">
              <Briefcase size={28} />
              <h3>Select a funding program to begin</h3>
              <p>The application questions and document requirements will appear after a program is selected.</p>
            </div>
          )}

          {selectedProgram && (
            <>
              <ApplicationPrompt title={prefill.prompt1} value={answer1} onChange={setAnswer1} />
              <ApplicationPrompt title={prefill.prompt2} value={answer2} onChange={setAnswer2} />
              <ApplicationPrompt title={prefill.prompt3} value={answer3} onChange={setAnswer3} />

              <div className="upload-workflow">
                <div className="workflow-banner">
                  <UploadCloud size={20} />
                  <span>Supporting documentation</span>
                </div>

                <select value={documentType} onChange={e => setDocumentType(e.target.value)}>
                  <option value="RESUME">Resume</option>
                  <option value="TRANSCRIPT">Transcript</option>
                  <option value="RECOMMENDATION_LETTER">Recommendation Letter</option>
                  <option value="FINANCIAL_DOCUMENT">Financial Document</option>
                  <option value="PORTFOLIO">Portfolio</option>
                  <option value="OTHER">Other</option>
                </select>

                <input
                  type="file"
                  multiple
                  onChange={e => setSelectedFiles(Array.from(e.target.files || []))}
                />

                {selectedFiles.length > 0 && (
                  <div className="selected-files">
                    {selectedFiles.map(file => (
                      <span key={file.name}>{file.name}</span>
                    ))}
                  </div>
                )}
              </div>

              <div className="button-row">
                <button className="secondary-button" onClick={() => createApplication("draft")}><Save size={16} /> {editingDraftId ? "Update Draft" : "Save as Draft"}</button>
                <button className="success-button" onClick={() => createApplication("submit")}><CheckCircle2 size={16} /> {editingDraftId ? "Submit Draft" : "Submit Application"}</button>
              </div>
            </>
          )}

          {message && <p className="success">{message}</p>}
        </div>
      )}

      <div className="table-card">
        {applications
          .filter(app => user.role === "APPLICANT" || app.status !== "DRAFT")
          .map(app => (
          <div className="row" key={app.id}>
            <div>
              <strong>{app.program?.name}</strong>
              <p>{app.applicant?.fullName} · {app.personalStatement?.slice(0, 120)}...</p>
            </div>
            <div className="row-actions">
              <span className="status">{app.status}</span>
              {user.role === "APPLICANT" && (
                <>
                  <button className="secondary-button" onClick={() => openApplication(app)}><Eye size={16} /> View</button>
                  {app.status === "DRAFT" && <button className="secondary-button" onClick={() => editDraft(app)}>Edit Draft</button>}
                </>
              )}

              {(user.role === "ADMIN" || user.role === "PROGRAM_MANAGER") && (
                <>
                  <button onClick={() => openApplication(app)}>Review</button>
                  <button onClick={() => moveStatus(app.id, "APPROVED")}>Approve</button>
                  <button onClick={() => moveStatus(app.id, "REJECTED")}>Reject</button>
                  <button onClick={() => moveStatus(app.id, "WAITLISTED")}>Waitlist</button>
                </>
              )}
            </div>
          </div>
        ))}
      </div>

      {selectedApplication && (
        <ApplicationDetailModal
          application={selectedApplication}
          documents={selectedDocs}
          reviews={selectedReviews}
          user={user}
          onUpdated={onCreated}
          onClose={() => {
            setSelectedApplication(null);
            setSelectedDocs([]);
            setSelectedReviews([]);
          }}
        />
      )}
    </div>
  );
}

function ApplicationPrompt({ title, value, onChange }: { title: string; value: string; onChange: (value: string) => void }) {
  return (
    <div className="prompt-block">
      <label>{title}</label>
      <textarea value={value} onChange={e => onChange(e.target.value)} />
    </div>
  );
}

function formatDateTime(value: string | null) {
  if (!value) return "Not submitted yet";
  return new Date(value).toLocaleString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "2-digit"
  });
}

function ApplicationDetailModal({
  application,
  documents,
  reviews,
  user,
  onUpdated,
  onClose
}: {
  application: Application;
  documents: ApplicationDocument[];
  reviews?: Review[];
  user: User;
  onUpdated: () => void;
  onClose: () => void;
}) {
  const [academicScore, setAcademicScore] = useState(18);
  const [leadershipScore, setLeadershipScore] = useState(18);
  const [financialNeedScore, setFinancialNeedScore] = useState(18);
  const [communityImpactScore, setCommunityImpactScore] = useState(18);
  const [essayScore, setEssayScore] = useState(18);
  const [recommendation, setRecommendation] = useState("APPROVE");
  const [feedback, setFeedback] = useState("Strong application with clear alignment to the program goals, meaningful leadership experience, and a thoughtful plan for community impact.");
  const [reviewMessage, setReviewMessage] = useState("");

  async function submitAdminReview() {
    await axios.post(`${API}/applications/${application.id}/reviews`, {
      academicScore,
      leadershipScore,
      financialNeedScore,
      communityImpactScore,
      essayScore,
      recommendation,
      feedback
    }, { headers: authHeaders() });

    setReviewMessage("Review saved successfully.");
    onUpdated();
  }
  return (
    <div className="modal-backdrop">
      <div className="modal">
        <div className="modal-header">
          <div>
            <h2>{application.program?.name}</h2>
            <p>{application.applicant?.fullName} · {application.applicant?.email}</p>
          </div>
          <button className="ghost" onClick={onClose}>Close</button>
        </div>

        <div className="detail-grid stacked-details">
          <Detail label="Status" value={application.status} />
          <Detail label="Submitted At" value={formatDateTime(application.submittedAt)} />
          <Detail label={getProgramPrefill(application.program?.name).prompt1} value={application.personalStatement} />
          <Detail label={getProgramPrefill(application.program?.name).prompt2} value={application.academicBackground} />
          <Detail label={getProgramPrefill(application.program?.name).prompt3} value={application.financialNeedStatement} />
        </div>

        <h3>Supporting Documents</h3>
        <div className="documents-list">
          {documents.length === 0 && <p className="muted">No documents uploaded.</p>}
          {documents.map(doc => (
            <div className="document-item" key={doc.id}>
              <div>
                <strong>{doc.documentType}</strong>
                <p>{doc.fileName}</p>
              </div>
              <a
                className="secondary-button document-link"
                href={`http://localhost:8080${doc.documentUrl}`}
                target="_blank"
                rel="noreferrer"
              >
                Open Document
              </a>
            </div>
          ))}
        </div>

        <h3>Reviewer Feedback</h3>
        <div className="reviews-list">
          {(!reviews || reviews.length === 0) && <p className="muted">No admin review submitted yet.</p>}
          {reviews?.map(review => (
            <div className="review-card" key={review.id}>
              <div className="review-card-header">
                <strong>{review.reviewer?.fullName || "Admin"}</strong>
                <span className="status">{review.recommendation}</span>
              </div>
              <div className="score-grid">
                <span>Academic: {review.academicScore}</span>
                <span>Leadership: {review.leadershipScore}</span>
                <span>Need: {review.financialNeedScore}</span>
                <span>Impact: {review.communityImpactScore}</span>
                <span>Essay: {review.essayScore}</span>
                <strong>Total: {review.totalScore}</strong>
              </div>
              <p>{review.feedback}</p>
            </div>
          ))}
        </div>

        {(user.role === "ADMIN" || user.role === "PROGRAM_MANAGER") && (
          <>
            <h3>Admin Review</h3>
            <div className="score-form">
              <ScoreInput label="Academic Score" value={academicScore} onChange={setAcademicScore} />
              <ScoreInput label="Leadership Score" value={leadershipScore} onChange={setLeadershipScore} />
              <ScoreInput label="Financial Need Score" value={financialNeedScore} onChange={setFinancialNeedScore} />
              <ScoreInput label="Community Impact Score" value={communityImpactScore} onChange={setCommunityImpactScore} />
              <ScoreInput label="Essay Score" value={essayScore} onChange={setEssayScore} />

              <div className="prompt-block">
                <label>Recommendation</label>
                <select value={recommendation} onChange={e => setRecommendation(e.target.value)}>
                  <option value="STRONG_APPROVE">Strong Approve</option>
                  <option value="APPROVE">Approve</option>
                  <option value="HOLD">Hold</option>
                  <option value="REJECT">Reject</option>
                  <option value="STRONG_REJECT">Strong Reject</option>
                </select>
              </div>

              <div className="prompt-block wide">
                <label>Internal Feedback</label>
                <textarea value={feedback} onChange={e => setFeedback(e.target.value)} />
              </div>
            </div>

            <div className="button-row">
              <button className="success-button" onClick={submitAdminReview}>Save Admin Review</button>
            </div>

            {reviewMessage && <p className="success">{reviewMessage}</p>}
          </>
        )}
      </div>
    </div>
  );
}

function Detail({ label, value }: { label: string; value?: string }) {
  return (
    <div className="detail-card">
      <span>{label}</span>
      <p>{value || "N/A"}</p>
    </div>
  );
}

function ScoreInput({ label, value, onChange }: { label: string; value: number; onChange: (value: number) => void }) {
  return (
    <div className="prompt-block">
      <label>{label}</label>
      <input
        type="number"
        min="0"
        max="20"
        value={value}
        onChange={e => onChange(Number(e.target.value))}
      />
    </div>
  );
}

function Notifications() {
  const [items, setItems] = useState<any[]>([]);

  useEffect(() => {
    axios.get(`${API}/notifications`, { headers: authHeaders() })
      .then(res => setItems(res.data))
      .catch(() => setItems([]));
  }, []);

  return (
    <div className="table-card">
      {items.length === 0 && <p className="muted">No notifications yet.</p>}
      {items.map(n => (
        <div className="row" key={n.id}>
          <div>
            <strong>{n.title}</strong>
            <p>{n.message}</p>
          </div>
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
      {items.length === 0 && <p className="muted">No audit activity has been recorded yet.</p>}
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
